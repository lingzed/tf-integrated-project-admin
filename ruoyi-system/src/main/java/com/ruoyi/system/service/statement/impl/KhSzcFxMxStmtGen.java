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
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 客户收支差分析明细表生成器
 */
@Component
public class KhSzcFxMxStmtGen extends StatementGenProcess {
    @Resource
    private StatementReadService statementReadService;
    @Resource
    private StatementWriteService statementWriteService;
    @Resource
    private StatementCfgService statementCfgService;
    @Resource
    private KhSzcFxMxStmtExtractor khSzcFxMxStmtExtractor;
    @Resource(name = "requestThreadPoolExecutor")
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Override
    public String confirmStmtTplName(StmtGenContext context) {
        return "TPL_KH_SZC_FX_MXB_" + context.corpCode().substring(0, 3);
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
        List<AssVo> assVoList = getAssVoList();
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        StatementQueryCfg queryCfg = stmtCfgWrapper.getObjQueryCfg();
        String subCorp = context.corpCode().substring(0, 3);
        RowColHeadIndex rowColHeadIndex = rcHeadIndexMap.get(subCorp);
        if (rowColHeadIndex == null) {
            throw new RuntimeException("行列头索引映射无对应项，key=" + subCorp);
        }
        Set<String> subjSet = rowColHeadIndex.getRowHeadIdx().keySet().stream()
                .filter(s -> !queryCfg.getRowHdQueryRemove().contains(s))
                .collect(Collectors.toSet());

        List<AuxBalance> curr = Collections.synchronizedList(new ArrayList<>());
        List<AuxBalance> pre = Collections.synchronizedList(new ArrayList<>());
        fetchByPeriod(curr, futures, context.periodStr(), subjSet, assVoList, queryCfg);
        fetchByPeriod(pre, futures, PeriodUtil.getPreviousPeriod(context.period()), subjSet, assVoList, queryCfg);

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        AuxBalanceDataWrapper auxBalanceDataWrapper = new AuxBalanceDataWrapper();
        auxBalanceDataWrapper.setCurrPeriod(curr);
        auxBalanceDataWrapper.setPrePeriod(pre);
        FinancialDataWrapper dataWrapper = resultMap.computeIfAbsent(subCorp, k -> new FinancialDataWrapper());
        dataWrapper.setAuxBalanceDataWrapper(auxBalanceDataWrapper);
        return resultMap;
    }

    @Override
    public byte[] writeData(File stmtTplFile, Map<String, RowColHeadIndex> rcHeadIndexMap, Map<String, FinancialDataWrapper> dataMap, StmtCfgWrapper stmtCfg, StmtGenContext context) throws IOException {
        return statementWriteService.coordinateWriter(stmtTplFile, dataMap, rcHeadIndexMap, stmtCfg, context, khSzcFxMxStmtExtractor);
    }

    private List<AssVo> getAssVoList() {
        return Stream.of("73", "J06Ass").map(s -> {
            AssVo assVo = new AssVo();
            assVo.setChecktypecode(s);
            return assVo;
        }).collect(Collectors.toList());
    }

    private void fetchByPeriod(List<AuxBalance> result, List<CompletableFuture<Void>> futures, String p, Set<String> subjSet, List<AssVo> assVoList, StatementQueryCfg queryCfg) {
        List<String> queryCorpList = queryCfg.getQueryCorpList();
        if (CollectionUtils.isEmpty(queryCorpList)) return;

        queryCorpList.forEach(corp -> {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                List<AuxBalance> abList = U8CApiUtil.queryAuxBalance(corp, p, p, subjSet, assVoList);
                if (CollectionUtils.isEmpty(abList)) return;
                result.addAll(abList);
            }, threadPoolTaskExecutor);
            futures.add(future);
        });
    }
}
