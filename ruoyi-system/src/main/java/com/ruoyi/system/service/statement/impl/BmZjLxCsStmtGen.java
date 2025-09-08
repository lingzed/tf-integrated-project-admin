package com.ruoyi.system.service.statement.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.enums.statement.StatementCfgType;
import com.ruoyi.common.enums.statement.StatementType;
import com.ruoyi.common.u8c.AssVo;
import com.ruoyi.system.domain.statement.StmtGenContext;
import com.ruoyi.common.u8c.warpper.AuxBalanceDataWrapper;
import com.ruoyi.common.u8c.warpper.FinancialDataWrapper;
import com.ruoyi.common.utils.PeriodUtil;
import com.ruoyi.common.utils.StmtRelatedUtil;
import com.ruoyi.common.utils.U8CApiUtil;
import com.ruoyi.system.domain.statement.RowColHeadIndex;
import com.ruoyi.system.domain.statement.cfg.*;
import com.ruoyi.system.service.StatementCfgService;
import com.ruoyi.system.service.statement.StatementReadService;
import com.ruoyi.system.service.statement.StatementWriteService;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 部门资金利息测试表生成
 */
@Component
public class BmZjLxCsStmtGen extends StatementGenProcess {
    public static final String SUPPLEMENT = "supplement";
    public static final String ORIGINAL_HEAD_INDEX_MAP = "originalHeadIndexMap";
    private static final Logger log = LoggerFactory.getLogger(BmZjLxCsStmtGen.class);

    @Resource
    private StatementReadService statementReadService;
    @Resource
    private StatementWriteService statementWriteService;
    @Resource
    private StatementCfgService statementCfgService;
    @Resource(name = "requestThreadPoolExecutor")
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;
    @Resource
    private BmZjLxCsStmtExtractor bmZjLxCsStmtExtractor;

    @Override
    public String confirmStmtTplName(StmtGenContext context) {
        StringBuilder stringBuilder = new StringBuilder("TPL_BM_ZJ_LX_CSB_");
        YearMonth period = context.period();
        String corpCode = context.corpCode();
        stringBuilder.append(corpCode);
        if (corpCode.equals("A0501") && period.getYear() == 2025) {
            stringBuilder.append("_SKIP2025M1TOM").append(period.getMonthValue() <= 4 ? "3" : "4");
        }

        return stringBuilder.toString();
    }

    @Override
    public StmtCfgWrapper loadStmtCfg(StmtGenContext context) {
        StatementType stmtType = StatementType.BM_ZJ_LX_CSB;
        String corpCode = context.corpCode();
        YearMonth period = context.period();
        // 索引配置key
        String indexKey = StmtRelatedUtil.getCfgCode(stmtType, StatementCfgType.ROW_COL_HEAD_INDEX_CFG, corpCode);
        String writeKey;     // 写入配置
        if (corpCode.equals("A0501") && period.getYear() > 2025) {
            writeKey = StmtRelatedUtil.getCfgCode(stmtType, StatementCfgType.SHEET_WRITE_CFG, corpCode);
        } else {
            String other = String.format("skip2025(1-%d)", period.getMonthValue() <= 4 ? 3 : 4);
            writeKey = StmtRelatedUtil.getCfgCode(stmtType, StatementCfgType.SHEET_WRITE_CFG, corpCode, other);
        }
        // 查询配置key
        String queryKey = StmtRelatedUtil.getCfgCode(stmtType, StatementCfgType.STATEMENT_QUERY_CFG, corpCode);
        String dataKey = StmtRelatedUtil.getCfgCode(stmtType, StatementCfgType.ROW_COL_HEAD_DATA_MAPPER_CFG, corpCode);

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
    public Map<String, RowColHeadIndex> getRowColHeadIndexMap(File stmtTplFile, StmtCfgWrapper stmtCfgWrapper,
                                                              StmtGenContext context) throws IOException {
        List<RowColHeadIndexCfg> cfg = stmtCfgWrapper.getListIndexCfg();
        Map<String, RowColHeadIndex> rcHeadIndexMap = statementReadService.extractRowColHead(stmtTplFile, cfg);
        context.put(ORIGINAL_HEAD_INDEX_MAP, rcHeadIndexMap);   // 保存原始行列头索引映射
        RowColHeadIndex rcHeadIndex = rcHeadIndexMap.get(context.corpCode());
        // 包装行列头索引映射
        Map<String, RowColHeadIndex> result = new HashMap<>();
        for (int i = 1; i <= context.period().getMonthValue(); i++) {
            result.put("m" + (i < 10 ? "0" + i : i), rcHeadIndex);
        }
        return result;
    }

    @Override
    public Boolean withDataTplHandle(StmtGenContext context) {
        return true;
    }

    // 数据模板为上年12月的报表
    @Override
    public String getStmtDataTplFilepath(StmtGenContext context) {
        String stmtDataTplFormat = super.getStmtDataTplFilepath(context);  // 输出文件的格式
        String endPeriod = PeriodUtil.endPeriod(PeriodUtil.lastYearPeriod(context.period()));    // 上年12月
        return String.format(stmtDataTplFormat, endPeriod);
    }

    @Override
    public Map<String, Map<String, String>> getRowColHeadMapData(File dataTplFile, StmtCfgWrapper stmtCfgWrapper,
                                                                 StmtGenContext context) throws IOException {
        return statementReadService.getRowColHeadMapData(dataTplFile, stmtCfgWrapper.getObjDataCfg());
    }

    @Override
    public Map<String, FinancialDataWrapper> fetchData(Map<String, RowColHeadIndex> rcHeadIndexMap, StmtCfgWrapper stmtCfgWrapper,
                                                       StmtGenContext context) {
        Map<String, FinancialDataWrapper> result = new HashMap<>();
        Map<String, FinancialDataWrapper> spResult = new HashMap<>();
        YearMonth period = context.period();
        String corpCode = context.corpCode();
        Map<String, RowColHeadIndex> original = context.get(ORIGINAL_HEAD_INDEX_MAP, new TypeReference<Map<String, RowColHeadIndex>>() {
        });
        RowColHeadIndex rowColHeadIndex = original.get(corpCode);
        if (rowColHeadIndex == null) {
            throw new RuntimeException(String.format("公司【%s】没有对应的行列头索引映射", corpCode));
        }
        boolean is2025 = period.getYear() <= 2025;
        int skipM = period.getMonthValue() <= 4 ? 3 : 4; // 4月跳过1-3月、5月及以后跳过1-4月

        List<CompletableFuture<Void>> futures = new ArrayList<>();
        toPeriodLoop(is2025, skipM, period, (currentP, m) -> {
            Set<String> subjSet = rowColHeadIndex.getColHeadIdx().keySet(); // 科目列表
            List<AssVo> deptAssVoList = getDeptAssVoList(rowColHeadIndex.getRowHeadIdx().keySet()); // 部门列表
            CompletableFuture<Void> future = getFuture(corpCode, currentP, currentP, subjSet, deptAssVoList, result, m);
            futures.add(future);
        });

        // 补充查询
        StatementQueryCfg qCfg = stmtCfgWrapper.getObjQueryCfg();
        toPeriodLoop(is2025, skipM, period, (currentP, m) -> {
            if (qCfg == null) return;
            Set<String> subjSet = qCfg.getQuerySubjList();
            List<AssVo> assVoList = assVoList(qCfg);
            CompletableFuture<Void> future = getFuture(corpCode, currentP, currentP, subjSet, assVoList, spResult, m);
            futures.add(future);
        });

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[]{})).join();
        context.put(SUPPLEMENT, spResult);
        return result;
    }

    @Override
    public byte[] writeData(File stmtTplFile, Map<String, RowColHeadIndex> rcHeadIndexMap,
                            Map<String, FinancialDataWrapper> dataMap,
                            StmtCfgWrapper stmtCfg,
                            StmtGenContext context) throws IOException {
        return statementWriteService
                .coordinateWriter(stmtTplFile, dataMap, rcHeadIndexMap, stmtCfg, context, bmZjLxCsStmtExtractor);
    }

    @Override
    public String saveDataTpl(byte[] dataByte, String outFile, StmtGenContext context) throws IOException {
        int month = context.period().getMonthValue();
        // 如果是12月，则保存数据模板
        if (month == 12) {
            String dataTempPath = RuoYiConfig.getStatementDataTempPath(context.statementTpl().getStatementType());
            Path dataTpl = Paths.get(dataTempPath, outFile);
            Files.write(dataTpl, dataByte, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            return dataTpl.toAbsolutePath().toString();
        }
        return "";
    }

    /**
     * 按期间进行循环，有跳过月
     * 传入要执行的方法，提供当月的期间和当月数
     * @param is2025
     * @param skipM
     * @param ym
     * @param consumer
     */
    private void toPeriodLoop(Boolean is2025, Integer skipM, YearMonth ym, BiConsumer<String, Integer> consumer) {
        int i = is2025 ? skipM + 1 : 1;
        for (; i <= ym.getMonthValue(); i++) {
            Integer m = i;
            consumer.accept(PeriodUtil.withMonth(ym, i), m);
        }
    }

    private CompletableFuture<Void> getFuture(String corpCode, String startP, String endP, Set<String> subjSet,
                                              List<AssVo> assVoList, Map<String, FinancialDataWrapper> result, int m) {
        return CompletableFuture.supplyAsync(() -> U8CApiUtil.queryAuxBalance(corpCode, startP, endP, subjSet, assVoList),
                        threadPoolTaskExecutor)
                .thenAccept(data -> {
                    if (CollectionUtils.isEmpty(data)) return;
                    FinancialDataWrapper financialDataWrapper = new FinancialDataWrapper();
                    AuxBalanceDataWrapper auxBalanceDataWrapper = new AuxBalanceDataWrapper();
                    auxBalanceDataWrapper.setCurrPeriod(data);
                    financialDataWrapper.setAuxBalanceDataWrapper(auxBalanceDataWrapper);
                    result.put("m" + (m < 10 ? "0" + m : m), financialDataWrapper);
                });
    }

    private List<AssVo> getDeptAssVoList(Set<String> deptCodes) {
        String deptListStr = String.join(",", deptCodes);
        AssVo assVo = new AssVo();
        assVo.setChecktypecode("2");
        assVo.setCheckvaluecode(deptListStr);
        return Collections.singletonList(assVo);
    }

    private List<AssVo> assVoList(StatementQueryCfg qCfg) {
        List<String> deptList = qCfg.getQueryDeptList();
        List<String> costList = qCfg.getQueryCostList();
        return Stream.of("2", "73").map(s -> {
            AssVo assVo = new AssVo();
            assVo.setChecktypecode(s);
            assVo.setCheckvaluecode(String.join(",", s.equals("2") ? deptList : costList));
            return assVo;
        }).collect(Collectors.toList());
    }
}
