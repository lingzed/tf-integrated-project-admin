package com.ruoyi.system.service.statement.impl;

import com.ruoyi.common.enums.statement.StatementCfgType;
import com.ruoyi.common.enums.statement.StatementTpl;
import com.ruoyi.common.u8c.AssVo;
import com.ruoyi.common.u8c.AuxBalance;
import com.ruoyi.common.u8c.SubjBalance;
import com.ruoyi.common.u8c.warpper.AuxBalanceDataWrapper;
import com.ruoyi.common.u8c.warpper.FinancialDataWrapper;
import com.ruoyi.common.u8c.warpper.SubjBalanceDataWrapper;
import com.ruoyi.common.utils.CollectionSplitter;
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
import org.apache.commons.collections4.MapUtils;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * 利润分解表生成器
 */
@Component
public class LrFjStmtGen extends StatementGenProcess {
    @Resource
    private StatementCfgService statementCfgService;
    @Resource
    private StatementReadService statementReadService;
    @Resource
    private StatementWriteService statementWriteService;
    @Resource(name = "requestThreadPoolExecutor")
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;
    @Resource
    private LrFjStmtExtractor lrFjStmtExtractor;

    @Override
    public String confirmStmtTplName(StmtGenContext context) {
        String pCorpCode = context.corpCode().substring(0, 3);
        context.setPCorpCode(pCorpCode);
        return "TPL_LR_FJB_" + pCorpCode;
    }

    @Override
    public StmtCfgWrapper loadStmtCfg(StmtGenContext context) {
        StatementTpl statementTpl = context.statementTpl();
        String indexKey = StmtRelatedUtil.getCfgCode(statementTpl, StatementCfgType.ROW_COL_HEAD_INDEX_CFG);
        String writeKey = StmtRelatedUtil.getCfgCode(statementTpl, StatementCfgType.SHEET_WRITE_CFG);
        String queryKey = StmtRelatedUtil.getCfgCode(statementTpl, StatementCfgType.STATEMENT_QUERY_CFG);

        List<RowColHeadIndexCfg> indexCfg = statementCfgService.getListStmtCfgCache(indexKey, RowColHeadIndexCfg.class);
        List<SheetWriteCfg> writeCfg = statementCfgService.getListStmtCfgCache(writeKey, SheetWriteCfg.class);
        Map<String, StatementQueryCfg> queryCfg = statementCfgService.getMapStmtCfgCache(queryKey, StatementQueryCfg.class);
        StmtCfgWrapper stmtCfgWrapper = new StmtCfgWrapper();
        stmtCfgWrapper.setListIndexCfg(indexCfg);
        stmtCfgWrapper.setListWriteCfg(writeCfg);
        stmtCfgWrapper.setMapQueryCfg(queryCfg);
        return stmtCfgWrapper;
    }

    @Override
    public Map<String, RowColHeadIndex> getRowColHeadIndexMap(File stmtTplFile, StmtCfgWrapper stmtCfgWrapper, StmtGenContext context) throws IOException {
        return statementReadService.extractRowColHead(stmtTplFile, stmtCfgWrapper.getListIndexCfg());
    }

    @Override
    public Map<String, FinancialDataWrapper> fetchData(Map<String, RowColHeadIndex> rcHeadIndexMap, StmtCfgWrapper stmtCfgWrapper, StmtGenContext context) {
        Map<String, FinancialDataWrapper> result = new HashMap<>();
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        String period = context.periodStr();
        Map<String, StatementQueryCfg> queryCfgMap = stmtCfgWrapper.getMapQueryCfg();
        rcHeadIndexMap.forEach((corp, rcHeadIndex) -> {
            StatementQueryCfg queryCfg = queryCfgMap.get(corp);
            List<List<String>> split = CollectionSplitter.splitCollection(rcHeadIndex.getColHeadIdx().keySet(), 4, 40);
            List<AssVo> assVoList = getAssVoList(rcHeadIndex.getRowHeadIdx().keySet(), queryCfg);

            List<AuxBalance> auxBalances = Collections.synchronizedList(new ArrayList<>());
            List<SubjBalance> subjBalances = Collections.synchronizedList(new ArrayList<>());
            // 辅助余额查询
            split.forEach(subjList -> {
                CompletableFuture<Void> future = CompletableFuture
                        .runAsync(() -> {
                            List<AuxBalance> abList = U8CApiUtil.queryAuxBalance(corp, period, period, new HashSet<>(subjList), assVoList);
                            if (CollectionUtils.isEmpty(abList)) return;
                            auxBalances.addAll(abList);
                        }, threadPoolTaskExecutor);
                futures.add(future);
            });
            // 科目余额查询
            subjBalanceList(corp, period, period, queryCfg, futures, subjBalances);

            FinancialDataWrapper dataWrapper = result.computeIfAbsent(corp, k -> new FinancialDataWrapper());
            AuxBalanceDataWrapper auxBalanceDataWrapper = new AuxBalanceDataWrapper();
            SubjBalanceDataWrapper subjBalanceDataWrapper = new SubjBalanceDataWrapper();
            auxBalanceDataWrapper.setCurrPeriod(auxBalances);
            subjBalanceDataWrapper.setCurrPeriod(subjBalances);
            dataWrapper.setAuxBalanceDataWrapper(auxBalanceDataWrapper);
            dataWrapper.setSubjBalanceDataWrapper(subjBalanceDataWrapper);
        });

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        return result;
    }

    @Override
    public byte[] writeData(File stmtTplFile, Map<String, RowColHeadIndex> rcHeadIndexMap, Map<String, FinancialDataWrapper> dataMap, StmtCfgWrapper stmtCfg, StmtGenContext context) throws IOException {
        return statementWriteService.coordinateWriter(stmtTplFile, dataMap, rcHeadIndexMap, stmtCfg, context, lrFjStmtExtractor);
    }

    private List<AssVo> getAssVoList(Set<String> deptSet, StatementQueryCfg queryCfg) {
        AssVo assVo = new AssVo();
        assVo.setChecktypecode("2");
        List<String> remove = queryCfg.getColHdQueryRemove();
        Set<String> depts = deptSet.stream()
                .filter(s -> !remove.contains(s))
                .collect(Collectors.toSet());
        assVo.setCheckvaluecode(String.join(",", depts));
        return Collections.singletonList(assVo);
    }

    private void subjBalanceList(String corpCode, String start, String end, StatementQueryCfg queryCfg,
                                 List<CompletableFuture<Void>> futures, List<SubjBalance> subjBalances) {
        String subjCodeFrom = queryCfg.getSubjCodeFrom();
        String subjCodeTo = queryCfg.getSubjCodeTo();
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            List<SubjBalance> abList = U8CApiUtil.subjBalanceQuery(corpCode, start, end, subjCodeFrom, subjCodeTo);
            if (CollectionUtils.isEmpty(abList)) return;
            subjBalances.addAll(abList);
        }, threadPoolTaskExecutor);
        futures.add(future);
    }

}
