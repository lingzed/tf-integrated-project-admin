package com.ruoyi.system.service.statement.impl;

import com.ruoyi.common.enums.statement.StatementCfgType;
import com.ruoyi.common.enums.statement.StatementTpl;
import com.ruoyi.common.u8c.AssVo;
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
 * 职能部门取数表生成器
 */
@Component
public class ZnBmQsStmtGen extends StatementGenProcess {
    @Resource
    private StatementReadService statementReadService;
    @Resource
    private StatementWriteService statementWriteService;
    @Resource
    private StatementCfgService statementCfgService;
    @Resource
    private ZnBmQsStmtExtractor znBmQsStmtExtractor;
    @Resource(name = "requestThreadPoolExecutor")
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Override
    public String confirmStmtTplName(StmtGenContext context) {
        return "TPL_ZN_BM_QSB_" + context.corpCode().charAt(0);
    }

    @Override
    public StmtCfgWrapper loadStmtCfg(StmtGenContext context) {
        StatementTpl statementTpl = context.statementTpl();
        String indexKey = StmtRelatedUtil.getCfgCode(statementTpl, StatementCfgType.ROW_COL_HEAD_INDEX_CFG);
        String writeKey = StmtRelatedUtil.getCfgCode(statementTpl, StatementCfgType.SHEET_WRITE_CFG);

        List<RowColHeadIndexCfg> indexCfg = statementCfgService.getListStmtCfgCache(indexKey, RowColHeadIndexCfg.class);
        List<SheetWriteCfg> writeCfg = statementCfgService.getListStmtCfgCache(writeKey, SheetWriteCfg.class);

        StmtCfgWrapper stmtCfgWrapper = new StmtCfgWrapper();
        stmtCfgWrapper.setListIndexCfg(indexCfg);
        stmtCfgWrapper.setListWriteCfg(writeCfg);
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
        rcHeadIndexMap.forEach((corpCode, rcHeadIndex) -> {
            FinancialDataWrapper dataWrapper = result.computeIfAbsent(corpCode, k -> new FinancialDataWrapper());
            List<AuxBalance> abList = Collections.synchronizedList(new ArrayList<>());
            Set<String> subjSet = rcHeadIndex.getColHeadIdx().keySet();
            List<AssVo> assVos = assVoList(rcHeadIndex);

            CollectionSplitter.splitCollection(subjSet, 2)
                    .forEach(subjList -> {
                        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                            List<AuxBalance> data = U8CApiUtil.queryAuxBalance(corpCode, period, period, new HashSet<>(subjList), assVos);
                            if (CollectionUtils.isEmpty(data)) return;
                            abList.addAll(data);
                        }, threadPoolTaskExecutor);
                        futures.add(future);
                    });
            AuxBalanceDataWrapper auxBalanceDataWrapper = new AuxBalanceDataWrapper();
            auxBalanceDataWrapper.setCurrPeriod(abList);
            dataWrapper.setAuxBalanceDataWrapper(auxBalanceDataWrapper);
        });

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        return result;
    }

    @Override
    public byte[] writeData(File stmtTplFile, Map<String, RowColHeadIndex> rcHeadIndexMap, Map<String, FinancialDataWrapper> dataMap, StmtCfgWrapper stmtCfg, StmtGenContext context) throws IOException {
        return statementWriteService.coordinateWriter(stmtTplFile, dataMap, rcHeadIndexMap, stmtCfg, context, znBmQsStmtExtractor);
    }

    private List<AssVo> assVoList(RowColHeadIndex rowColHeadIndex) {
        AssVo assVo = new AssVo();
        assVo.setChecktypecode("2");
        String join = String.join(",", rowColHeadIndex.getRowHeadIdx().keySet());
        assVo.setCheckvaluecode(join);
        return Collections.singletonList(assVo);
    }
}
