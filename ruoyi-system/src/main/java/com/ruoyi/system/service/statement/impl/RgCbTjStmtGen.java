package com.ruoyi.system.service.statement.impl;

import com.ruoyi.common.enums.statement.StatementCfgType;
import com.ruoyi.common.enums.statement.StatementTpl;
import com.ruoyi.common.u8c.AssVo;
import com.ruoyi.common.u8c.AuxBalance;
import com.ruoyi.common.u8c.warpper.AuxBalanceDataWrapper;
import com.ruoyi.common.u8c.warpper.FinancialDataWrapper;
import com.ruoyi.common.utils.PeriodUtil;
import com.ruoyi.common.utils.StmtRelatedUtil;
import com.ruoyi.common.utils.U8CApiUtil;
import com.ruoyi.system.domain.statement.RowColHeadIndex;
import com.ruoyi.system.domain.statement.StmtGenContext;
import com.ruoyi.system.domain.statement.cfg.RowColHeadIndexCfg;
import com.ruoyi.system.domain.statement.cfg.SheetWriteCfg;
import com.ruoyi.system.domain.statement.cfg.StatementQueryCfg;
import com.ruoyi.system.domain.statement.cfg.StmtCfgWrapper;
import com.ruoyi.system.service.StatementCfgService;
import com.ruoyi.system.service.statement.StatementReadService;
import com.ruoyi.system.service.statement.StatementWriteService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * 人工成本统计表生成器
 */
@Component
public class RgCbTjStmtGen extends StatementGenProcess {
    @Resource
    private StatementCfgService statementCfgService;
    @Resource
    private StatementReadService statementReadService;
    @Resource
    private StatementWriteService statementWriteService;
    @Resource(name = "requestThreadPoolExecutor")
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;
    @Resource
    private RgCbTjStmtExtractor rgCbTjStmtExtractor;

    @Override
    public String confirmStmtTplName(StmtGenContext context) {
        return "TPL_RG_CB_TJB_A";
    }

    @Override
    public StmtCfgWrapper loadStmtCfg(StmtGenContext context) {
        StatementTpl statementTpl = context.statementTpl();
        String indexKey = StmtRelatedUtil.getCfgCode(statementTpl, StatementCfgType.ROW_COL_HEAD_INDEX_CFG);
        String writeKey = StmtRelatedUtil.getCfgCode(statementTpl, StatementCfgType.SHEET_WRITE_CFG);
        String queryKey = StmtRelatedUtil.getCfgCode(statementTpl, StatementCfgType.STATEMENT_QUERY_CFG);

        List<RowColHeadIndexCfg> indexCfg = statementCfgService.getListStmtCfgCache(indexKey, RowColHeadIndexCfg.class);
        List<SheetWriteCfg> writeCfg = statementCfgService.getListStmtCfgCache(writeKey, SheetWriteCfg.class);
        StatementQueryCfg queryCfg = statementCfgService.getObjStmtCfgCache(queryKey, StatementQueryCfg.class);

        StmtCfgWrapper stmtCfgWrapper = new StmtCfgWrapper();
        stmtCfgWrapper.setListIndexCfg(indexCfg);
        stmtCfgWrapper.setListWriteCfg(writeCfg);
        stmtCfgWrapper.setObjQueryCfg(queryCfg);
        return stmtCfgWrapper;
    }

    @Override
    public Map<String, RowColHeadIndex> getRowColHeadIndexMap(File stmtTplFile, StmtCfgWrapper stmtCfgWrapper, StmtGenContext context) throws IOException {
        return statementReadService.extractRowColHead(stmtTplFile, stmtCfgWrapper.getListIndexCfg());
    }

    @Override
    public Map<String, FinancialDataWrapper> fetchData(Map<String, RowColHeadIndex> rcHeadIndexMap, StmtCfgWrapper stmtCfgWrapper, StmtGenContext context) {
        Map<String, FinancialDataWrapper> resultMap = new HashMap<>();
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        rcHeadIndexMap.forEach((corp, rcHeadIndex) -> {
            FinancialDataWrapper dataWrapper = resultMap.computeIfAbsent(corp, k -> new FinancialDataWrapper());
            List<AuxBalance> first = Collections.synchronizedList(new ArrayList<>());   // 本年1月1列表
            List<AuxBalance> curr = Collections.synchronizedList(new ArrayList<>());    // 本期列表

            Set<String> subjSet = rcHeadIndex.getColHeadIdx().keySet();
            Set<String> deptSet = rcHeadIndex.getRowHeadIdx().keySet();
            List<AssVo> assVoList = getAssVoList(deptSet);

            // 本年1月
            byPeriod(corp, PeriodUtil.firstPeriod(context.period()), subjSet, assVoList, first, futures);
            // 本期
            byPeriod(corp, context.periodStr(), subjSet, assVoList, curr, futures);

            AuxBalanceDataWrapper auxBalanceDataWrapper = new AuxBalanceDataWrapper();
            auxBalanceDataWrapper.setPeriod1(first);
            auxBalanceDataWrapper.setCurrPeriod(curr);
            dataWrapper.setAuxBalanceDataWrapper(auxBalanceDataWrapper);
            resultMap.put(corp, dataWrapper);
        });

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        return resultMap;
    }

    @Override
    public byte[] writeData(File stmtTplFile, Map<String, RowColHeadIndex> rcHeadIndexMap, Map<String, FinancialDataWrapper> dataMap, StmtCfgWrapper stmtCfg, StmtGenContext context) throws IOException {
        return statementWriteService.coordinateWriter(stmtTplFile, dataMap, rcHeadIndexMap, stmtCfg, context, rgCbTjStmtExtractor);
    }

    private List<AssVo> getAssVoList(Set<String> deptSet) {
        AssVo assVo = new AssVo();
        assVo.setChecktypecode("2");
        assVo.setCheckvaluecode(String.join(",", deptSet));
        return Collections.singletonList(assVo);
    }

    private void byPeriod(String corp, String period, Set<String> subjSet, List<AssVo> assVoList,
                          List<AuxBalance> result, List<CompletableFuture<Void>> futures) {
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            List<AuxBalance> abList = U8CApiUtil.queryAuxBalance(corp, period, period, subjSet, assVoList);
            if (CollectionUtils.isEmpty(abList)) return;
            result.addAll(abList);
        }, threadPoolTaskExecutor);
        futures.add(future);
    }
}
