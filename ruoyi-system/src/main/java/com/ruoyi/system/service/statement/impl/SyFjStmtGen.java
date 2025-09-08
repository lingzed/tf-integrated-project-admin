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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * 损益分解表生成器
 */
@Component
public class SyFjStmtGen extends StatementGenProcess {
    private static final Logger log = LoggerFactory.getLogger(SyFjStmtGen.class);
    @Resource
    private StatementCfgService statementCfgService;
    @Resource
    private StatementReadService statementReadService;
    @Resource
    private StatementWriteService statementWriteService;
    @Resource(name = "requestThreadPoolExecutor")
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;
    @Resource
    private SyFjStmtExtractor syFjStmtExtractor;

    @Override
    public String confirmStmtTplName(StmtGenContext context) {
        String corpCode = context.corpCode();
        return "TPL_SY_FJB_" + corpCode.charAt(0);
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
        String special = "A0101";
        String period = context.periodStr();
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        Map<String, FinancialDataWrapper> data = new HashMap<>();
        FinancialDataWrapper dataA0101 = data.computeIfAbsent(special, k -> new FinancialDataWrapper());
        List<AuxBalance> abResult = Collections.synchronizedList(new ArrayList<>());

        processA0101(rcHeadIndexMap.get(special), special, period, futures, abResult);
        processOther(data, rcHeadIndexMap, special, futures, stmtCfgWrapper.getObjQueryCfg(), period);
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[]{})).join();

        AuxBalanceDataWrapper auxBalanceDataWrapper = new AuxBalanceDataWrapper();
        auxBalanceDataWrapper.setCurrPeriod(abResult);
        dataA0101.setAuxBalanceDataWrapper(auxBalanceDataWrapper);
        return data;
    }

    @Override
    public byte[] writeData(File stmtTplFile, Map<String, RowColHeadIndex> rcHeadIndexMap, Map<String, FinancialDataWrapper> dataMap, StmtCfgWrapper stmtCfg, StmtGenContext context) throws IOException {
        return statementWriteService.coordinateWriter(stmtTplFile, dataMap, rcHeadIndexMap, stmtCfg, context, syFjStmtExtractor);
    }

    private void processA0101(RowColHeadIndex rcHeadIndex, String corpCode, String period,
                              List<CompletableFuture<Void>> futures, List<AuxBalance> abResult) {
        if (rcHeadIndex == null) return;
        Set<String> subjSet = rcHeadIndex.getColHeadIdx().keySet();
        Set<String> deptSet = rcHeadIndex.getRowHeadIdx().keySet();
        List<List<String>> lists = CollectionSplitter.splitCollection(deptSet, 4);
        lists.forEach(deptList -> {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                List<AuxBalance> abList = U8CApiUtil.queryAuxBalance(corpCode, period, period, subjSet, getAssVoList(deptList));
                if (CollectionUtils.isEmpty(abList)) return;
                abResult.addAll(abList);
            }, threadPoolTaskExecutor);
            futures.add(future);
        });
    }

    private void processOther(Map<String, FinancialDataWrapper> data, Map<String, RowColHeadIndex> rcHeadIndexMap,
                              String skip, List<CompletableFuture<Void>> futures, StatementQueryCfg queryCfg, String period) {
        if (queryCfg == null) return;
        List<String> remove = queryCfg.getRowHdQueryRemove();
        String from = queryCfg.getSubjCodeFrom();
        String to = queryCfg.getSubjCodeTo();
        rcHeadIndexMap.forEach((corpCode, rcHeadIndex) -> {
            if (corpCode.equals(skip)) return;
            FinancialDataWrapper dataWrapper = data.computeIfAbsent(corpCode, k -> new FinancialDataWrapper());

            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                // 科目余额查询
                List<SubjBalance> sbList = U8CApiUtil.subjBalanceQuery(corpCode, period, period, from, to);
                if (CollectionUtils.isNotEmpty(sbList)) {
                    SubjBalanceDataWrapper subjBalanceDataWrapper = new SubjBalanceDataWrapper();
                    subjBalanceDataWrapper.setCurrPeriod(sbList);
                    dataWrapper.setSubjBalanceDataWrapper(subjBalanceDataWrapper);
                }

                // 辅助余额查询
                if (rcHeadIndex == null) return;
                List<String> deptList = rcHeadIndex.getRowHeadIdx().keySet().stream()
                        .filter(s -> !remove.contains(s)).collect(Collectors.toList());
                if (CollectionUtils.isEmpty(deptList)) return;
                List<AuxBalance> abList = U8CApiUtil.queryAuxBalance(corpCode, period, period, rcHeadIndex.getColHeadIdx().keySet(),
                        getAssVoList(deptList));
                if (CollectionUtils.isEmpty(abList)) return;
                AuxBalanceDataWrapper auxBalanceDataWrapper = new AuxBalanceDataWrapper();
                auxBalanceDataWrapper.setCurrPeriod(abList);
                dataWrapper.setAuxBalanceDataWrapper(auxBalanceDataWrapper);
            }, threadPoolTaskExecutor);

            futures.add(future);
        });
    }

    private List<AssVo> getAssVoList(List<String> deptList) {
        List<AssVo> res = new ArrayList<>();
        AssVo assVo = new AssVo();
        assVo.setChecktypecode("2");
        assVo.setCheckvaluecode(String.join(",", deptList));
        res.add(assVo);
        return res;
    }
}
