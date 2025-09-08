package com.ruoyi.system.service.statement.impl;

import com.ruoyi.common.enums.statement.StatementCfgType;
import com.ruoyi.common.enums.statement.StatementTpl;
import com.ruoyi.common.u8c.SubjBalance;
import com.ruoyi.common.u8c.query.SubjBalanceQuery;
import com.ruoyi.common.u8c.warpper.FinancialDataWrapper;
import com.ruoyi.common.u8c.warpper.SubjBalanceDataWrapper;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Component
public class RgCbAndSjStmtGen extends StatementGenProcess {
    @Resource
    private StatementCfgService statementCfgService;
    @Resource
    private StatementReadService statementReadService;
    @Resource(name = "requestThreadPoolExecutor")
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;
    @Resource
    private RgCbAndSJStmtExtractor rgCbAndSJStmtExtractor;
    @Resource
    private StatementWriteService statementWriteService;

    @Override
    public String confirmStmtTplName(StmtGenContext context) {
        return "TPL_RG_CB_RJ_JCB_A";
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
        StatementQueryCfg queryCfg = stmtCfgWrapper.getObjQueryCfg();
        String from = queryCfg.getSubjCodeFrom();
        String to = queryCfg.getSubjCodeTo();
        String period = context.periodStr();

        List<CompletableFuture<Void>> futures = new ArrayList<>();
        rcHeadIndexMap.keySet().forEach(corp -> {
            FinancialDataWrapper dataWrapper = resultMap.computeIfAbsent(corp, k -> new FinancialDataWrapper());
            SubjBalanceDataWrapper subjBalanceDataWrapper = new SubjBalanceDataWrapper();
            dataWrapper.setSubjBalanceDataWrapper(subjBalanceDataWrapper);

            CompletableFuture<Void> future = CompletableFuture
                    .runAsync(() -> {
                        List<SubjBalance> sbList = U8CApiUtil.subjBalanceQuery(corp, period, period, from, to);
                        if (CollectionUtils.isEmpty(sbList)) return;
                        subjBalanceDataWrapper.setCurrPeriod(sbList);
                    }, threadPoolTaskExecutor);
            futures.add(future);
        });

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        return resultMap;
    }

    @Override
    public byte[] writeData(File stmtTplFile, Map<String, RowColHeadIndex> rcHeadIndexMap, Map<String, FinancialDataWrapper> dataMap, StmtCfgWrapper stmtCfg, StmtGenContext context) throws IOException {
        return statementWriteService.coordinateWriter(stmtTplFile, dataMap, rcHeadIndexMap, stmtCfg, context, rgCbAndSJStmtExtractor);
    }
}
