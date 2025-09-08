package com.ruoyi.system.service.statement.impl;

import com.ruoyi.common.enums.statement.StatementCfgType;
import com.ruoyi.common.enums.statement.StatementTpl;
import com.ruoyi.common.u8c.AssVo;
import com.ruoyi.common.u8c.AuxAcctProject;
import com.ruoyi.common.u8c.AuxBalance;
import com.ruoyi.common.u8c.warpper.AuxBalanceDataWrapper;
import com.ruoyi.common.u8c.warpper.FinancialDataWrapper;
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
import java.util.stream.Stream;

/**
 * 客户余额表生成器
 */
@Component
public class KhYeStmtGen extends StatementGenProcess {
    @Resource
    private StatementReadService statementReadService;
    @Resource
    private StatementWriteService statementWriteService;
    @Resource
    private StatementCfgService statementCfgService;
    @Resource
    private KhYeStmtExtractor khYeStmtExtractor;
    @Resource(name = "requestThreadPoolExecutor")
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Override
    public String confirmStmtTplName(StmtGenContext context) {
        String pCorpCode = context.corpCode().substring(0, 3);
        context.setPCorpCode(pCorpCode);
        return "TPL_KH_YEB_" + pCorpCode;
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
        String pCorpCode = context.pCorpCode();

        StatementQueryCfg queryCfg = stmtCfgWrapper.getObjQueryCfg();
        Set<String> subjSet = rcHeadIndexMap.get(pCorpCode).getRowHeadIdx()
                .keySet().stream().filter(subj -> !queryCfg.getRowHdQueryRemove().contains(subj)).collect(Collectors.toSet());
        // 科目列表分段
        List<List<String>> lists = CollectionSplitter.splitCollection(subjSet, 3);

        String period = context.periodStr();
        List<String> corpList = queryCfg.getQueryCorpList();
        // 主列表
        List<AuxBalance> main = Collections.synchronizedList(new ArrayList<>());
        // 补充列表
        List<AuxBalance> supplement = Collections.synchronizedList(new ArrayList<>());
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (String corpCode : corpList) {
            for (List<String> subjList : lists) {
                // 主查询
                CompletableFuture<Void> future = CompletableFuture
                        .runAsync(() -> {
                            List<AuxBalance> abList = U8CApiUtil.queryAuxBalance(corpCode, period, period, new HashSet<>(subjList), getAssVoList("73", "J06Ass"), 60);
                            if (CollectionUtils.isEmpty(abList)) return;
                            main.addAll(abList);
                        }, threadPoolTaskExecutor);
                addFuture(futures, future);
            }
            // 补充查询
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                List<AuxBalance> abList = U8CApiUtil.queryAuxBalance(corpCode, period, period, queryCfg.getQuerySubjList(), getAssVoList("73", "2"), 300);
                if (CollectionUtils.isEmpty(abList)) return;
                supplement.addAll(abList);
            }, threadPoolTaskExecutor);
            addFuture(futures, future);
        }

        if (!futures.isEmpty()) {
            aWait(futures);
        }

        AuxBalanceDataWrapper auxBalanceDataWrapper = new AuxBalanceDataWrapper();
        auxBalanceDataWrapper.setMain(main);
        auxBalanceDataWrapper.setSupplement(supplement);
        FinancialDataWrapper dataWrapper = resultMap.computeIfAbsent(pCorpCode, k -> new FinancialDataWrapper());
        dataWrapper.setAuxBalanceDataWrapper(auxBalanceDataWrapper);
        return resultMap;
    }

    private void addFuture(List<CompletableFuture<Void>> futures, CompletableFuture<Void> future) {
        if (futures.size() == 10) {
            aWait(futures);
        }
        futures.add(future);
    }

    private void aWait(List<CompletableFuture<Void>> futures) {
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        futures.clear();
    }

    @Override
    public byte[] writeData(File stmtTplFile, Map<String, RowColHeadIndex> rcHeadIndexMap, Map<String, FinancialDataWrapper> dataMap, StmtCfgWrapper stmtCfg, StmtGenContext context) throws IOException {
        return statementWriteService.coordinateWriter(stmtTplFile, dataMap, rcHeadIndexMap, stmtCfg, context, khYeStmtExtractor);
    }

    private List<AssVo> getAssVoList(String... auxCode) {
        return Stream.of(auxCode).map(e -> {
            AssVo assVo = new AssVo();
            assVo.setChecktypecode(e);
            return assVo;
        }).collect(Collectors.toList());
    }
}
