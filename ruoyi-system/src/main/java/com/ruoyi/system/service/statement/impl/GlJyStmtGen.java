package com.ruoyi.system.service.statement.impl;

import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.enums.statement.StatementCfgType;
import com.ruoyi.common.enums.statement.StatementTpl;
import com.ruoyi.common.u8c.Detail;
import com.ruoyi.common.u8c.Voucher;
import com.ruoyi.common.u8c.warpper.AuxBalanceDataWrapper;
import com.ruoyi.common.u8c.warpper.DetailDataWrapper;
import com.ruoyi.common.u8c.warpper.FinancialDataWrapper;
import com.ruoyi.common.utils.DateFormatUtil;
import com.ruoyi.common.utils.PeriodUtil;
import com.ruoyi.common.utils.StmtRelatedUtil;
import com.ruoyi.common.utils.U8CApiUtil;
import com.ruoyi.system.domain.statement.RowColHeadIndex;
import com.ruoyi.system.domain.statement.StmtGenContext;
import com.ruoyi.system.domain.statement.cfg.*;
import com.ruoyi.system.service.StatementCfgService;
import com.ruoyi.system.service.statement.StatementReadService;
import com.ruoyi.system.service.statement.StatementWriteService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.SetUtils;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.YearMonth;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;

/**
 * 关联校验生成器
 */
@Component
public class GlJyStmtGen extends StatementGenProcess {
    @Resource
    private StatementReadService statementReadService;
    @Resource
    private StatementWriteService statementWriteService;
    @Resource
    private StatementCfgService statementCfgService;
    @Resource
    private GlJyStmtExtractor glJyStmtExtractor;
    @Resource
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Override
    public String confirmStmtTplName(StmtGenContext context) {
        String corpCode = context.corpCode();
        return "TPL_GL_JYB_" + corpCode.substring(0, 3);
    }

    @Override
    public StmtCfgWrapper loadStmtCfg(StmtGenContext context) {
        StatementTpl statementTpl = context.statementTpl();
        String indexKey = StmtRelatedUtil.getCfgCode(statementTpl, StatementCfgType.ROW_COL_HEAD_INDEX_CFG);
        String writeKey = StmtRelatedUtil.getCfgCode(statementTpl, StatementCfgType.SHEET_WRITE_CFG);
        String queryKey = StmtRelatedUtil.getCfgCode(statementTpl, StatementCfgType.STATEMENT_QUERY_CFG);
        String dataKey = StmtRelatedUtil.getCfgCode(statementTpl, StatementCfgType.ROW_COL_HEAD_DATA_MAPPER_CFG);

        List<RowColHeadIndexCfg> indexCfg = statementCfgService.getListStmtCfgCache(indexKey, RowColHeadIndexCfg.class);
        List<SheetWriteCfg> writeCfg = statementCfgService.getListStmtCfgCache(writeKey, SheetWriteCfg.class);
        StatementQueryCfg queryCfg = statementCfgService.getObjStmtCfgCache(queryKey, StatementQueryCfg.class);
        RowColHeadDataMapperCfg dataCfg = statementCfgService.getObjStmtCfgCache(dataKey, RowColHeadDataMapperCfg.class);

        StmtCfgWrapper stmtCfgWrapper = new StmtCfgWrapper();
        stmtCfgWrapper.setListIndexCfg(indexCfg);
        stmtCfgWrapper.setListWriteCfg(writeCfg);
        stmtCfgWrapper.setObjQueryCfg(queryCfg);
        stmtCfgWrapper.setObjDataCfg(dataCfg);
        return stmtCfgWrapper;
    }

    @Override
    public Map<String, RowColHeadIndex> getRowColHeadIndexMap(File stmtTplFile, StmtCfgWrapper stmtCfgWrapper, StmtGenContext context) throws IOException {
        return statementReadService.extractRowColHead(stmtTplFile, stmtCfgWrapper.getListIndexCfg());
    }

    @Override
    public Boolean withDataTplHandle(StmtGenContext context) {
        return context.periodMonth() != 1;
    }

    @Override
    public String getStmtDataTplFilepath(StmtGenContext context) {
        String dataTplFilepath = super.getStmtDataTplFilepath(context);
        String prePeriod = PeriodUtil.getPreviousPeriod(context.period());
        // 数据模板为上期
        return String.format(dataTplFilepath, prePeriod);
    }

    @Override
    public Map<String, Map<String, String>> getRowColHeadMapData(File dataTplFile, StmtCfgWrapper stmtCfgWrapper, StmtGenContext context) throws IOException {
        return statementReadService.getRowColHeadMapData(dataTplFile, stmtCfgWrapper.getObjDataCfg());
    }

    @Override
    public Map<String, FinancialDataWrapper> fetchData(Map<String, RowColHeadIndex> rcHeadIndexMap, StmtCfgWrapper stmtCfgWrapper, StmtGenContext context) {
        Map<String, FinancialDataWrapper> result = new HashMap<>();
        String subCorp = context.corpCode().substring(0, 3);
        String periodStr = context.periodStr();
        RowColHeadIndex rcHead = rcHeadIndexMap.get(subCorp);
        Set<String> subjSet = getSubjSet(rcHead);
        List<String> corpList = stmtCfgWrapper.getObjQueryCfg().getQueryCorpList();
        int pageSize = 500;

        FinancialDataWrapper dataWrapper = result.computeIfAbsent(subCorp, k -> new FinancialDataWrapper());
        List<Detail> detailList = Collections.synchronizedList(new ArrayList<>());

        // 所有数据聚合到同一个 detailList
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (String corp : corpList) {
            for (String subjCode : subjSet) {
                String start = periodStr + "-01";
                String end = DateFormatUtil.formatToEnd(periodStr);
                // 预查询，当前页会尽可能拉到最大(10000)，这样只会返回实际的数据总数，然后再通过这个总数计算出总页码
                long total = U8CApiUtil.getVoucherTotal(corp, subjCode, start, end);
                long pageTotal = (total + pageSize - 1) / pageSize;
                for (long i = 1; i <= pageTotal; i++) {
                    int page = (int) i;
                    CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                        List<Detail> dataList = getDetailList(subjSet, corp, subjCode, start, end, page, pageSize);
                        if (CollectionUtils.isEmpty(dataList)) return;
                        detailList.addAll(dataList);
                    }, threadPoolTaskExecutor);
                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    futures.add(future);
                }
            }
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        DetailDataWrapper detailDataWrapper = new DetailDataWrapper();
        detailDataWrapper.setCurrPeriod(detailList);
        dataWrapper.setDetailDataWrapper(detailDataWrapper);
        return result;
    }


    @Override
    public byte[] writeData(File stmtTplFile, Map<String, RowColHeadIndex> rcHeadIndexMap, Map<String, FinancialDataWrapper> dataMap, StmtCfgWrapper stmtCfg, StmtGenContext context) throws IOException {
        return statementWriteService
                .coordinateWriter(stmtTplFile, dataMap, rcHeadIndexMap, stmtCfg, context, glJyStmtExtractor);
    }

    @Override
    public String saveDataTpl(byte[] dataByte, String outFile, StmtGenContext context) throws IOException {
        StatementTpl statementTpl = context.statementTpl();
        Path dataTplRootPath = Paths.get(RuoYiConfig.getStatementDataTempPath(statementTpl.getStatementType()));
        if (Files.notExists(dataTplRootPath)) {
            Files.createDirectories(dataTplRootPath);
        }
        Path path = dataTplRootPath.resolve(outFile);
        Files.write(path, dataByte, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        return path.toAbsolutePath().toString();
    }

    private static Set<String> getSubjSet(RowColHeadIndex rcHead) {
        return rcHead.getRowHeadIdx().keySet().stream()
                .filter(k -> k.contains("_"))
                .map(k -> k.substring(0, k.indexOf("_"))).collect(Collectors.toSet());
    }

    private static List<Detail> getDetailList(Set<String> subjSet, String corpCode, String subjCode, String start, String end, int page, int pageSize) {
        List<Detail> res = new ArrayList<>();
        List<Voucher> voucher = U8CApiUtil.queryVoucher(corpCode, subjCode, start, end, page, pageSize, 120);
        voucher.stream().filter(vc -> {
            String explanation = vc.getExplanation();
            // 计收结尾 && 计提开头，成本结尾
            return explanation.endsWith("计收")
                    || (explanation.startsWith("计提") && explanation.endsWith("成本"));
        }).forEach(v -> {
            List<Detail> dList = v.getDetail().stream()
                    .filter(d -> subjSet.contains(d.getAccsubj_code()))
                    .collect(Collectors.toList());
            res.addAll(dList);
        });
        return res;
    }
}
