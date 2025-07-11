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
import java.time.YearMonth;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * 各专业公司各省份收入情况统计表生成器
 */
@Component
public class GgsGsfSrQkTjStmtGen extends StatementGenProcess {
    @Resource
    private StatementReadService statementReadService;
    @Resource
    private StatementWriteService statementWriteService;
    @Resource
    private StatementCfgService statementCfgService;
    @Resource
    private GgsGsfSrQkTjStmtExtractor ggsGsfSrQkTjStmtExtractor;
    @Resource
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;


    @Override
    public String confirmStmtTplName(StmtGenContext context) {
        String corpCode = context.corpCode();
        String substring = corpCode.substring(0, 1);
        return "TPL_GGS_GSF_SR_QK_TJB_" + substring;
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
        List<RowColHeadIndexCfg> listIndexCfg = stmtCfgWrapper.getListIndexCfg();
        return statementReadService.extractRowColHead(stmtTplFile, listIndexCfg);
    }

    @Override
    public Map<String, FinancialDataWrapper> fetchData(Map<String, RowColHeadIndex> rcHeadIndexMap, StmtCfgWrapper stmtCfgWrapper, StmtGenContext context) {
        Map<String, FinancialDataWrapper> result = new HashMap<>();
        String period = context.periodStr();
        String lastPeriod = PeriodUtil.lastYearPeriod(period);    // 上年同期
        boolean gt2025 = context.periodYear() > 2025;

        List<CompletableFuture<Void>> futures = new ArrayList<>();
        rcHeadIndexMap.forEach((corp, rcHead) -> {
            CompletableFuture<Void> future = CompletableFuture
                    .supplyAsync(() -> fetch(corp, rcHead, gt2025, period, lastPeriod), threadPoolTaskExecutor)
                    .thenAccept(data -> result.put(corp, data));
            futures.add(future);
        });

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[]{})).join();
        return result;
    }

    @Override
    public byte[] writeData(File stmtTplFile, Map<String, RowColHeadIndex> rcHeadIndexMap,
                            Map<String, FinancialDataWrapper> dataMap, StmtCfgWrapper stmtCfg, StmtGenContext context) throws IOException {
        return statementWriteService
                .coordinateWriter(stmtTplFile, dataMap, rcHeadIndexMap, stmtCfg, context, ggsGsfSrQkTjStmtExtractor);
    }

    private FinancialDataWrapper fetch(String corp, RowColHeadIndex rcHead, Boolean gt2025, String period, String lastPeriod) {
        FinancialDataWrapper financialDataWrapper = new FinancialDataWrapper();

        // 解析出部门列表和科目列表
        Set<String> subjSet = new HashSet<>();
        Set<String> deptSet = new HashSet<>();
        rcHead.getRowHeadIdx().keySet().forEach(s -> {
            String[] split = s.split("_");
            subjSet.add(split[0]);
            if (split.length == 2) {
                deptSet.add(split[1]);
            }
        });

        List<AssVo> assVoList = getAssVoList(rcHead.getColHeadIdx().keySet(), deptSet);

        List<CompletableFuture<Void>> futures = new ArrayList<>();
        AuxBalanceDataWrapper auxBalanceDataWrapper = new AuxBalanceDataWrapper();
        // 上年同期的，24年没有数据，所以不请求
        if (gt2025) {
            CompletableFuture<Void> future = CompletableFuture
                    .supplyAsync(() -> U8CApiUtil.queryAuxBalance(corp, lastPeriod, lastPeriod, subjSet, assVoList),
                            threadPoolTaskExecutor)
                    .thenAccept(abList -> {
                        if (CollectionUtils.isNotEmpty(abList)) {
                            auxBalanceDataWrapper.setPreYearSamePeriod(abList); // 上年同期
                        }
                    });
            futures.add(future);
        }
        // 本年本期的
        CompletableFuture<Void> future = CompletableFuture
                .supplyAsync(() -> U8CApiUtil.queryAuxBalance(corp, period, period, subjSet, assVoList),
                        threadPoolTaskExecutor)
                .thenAccept(abList -> {
                    if (CollectionUtils.isNotEmpty(abList)) {
                        auxBalanceDataWrapper.setCurrPeriod(abList);    // 本期
                    }
                });
        futures.add(future);
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[]{})).join();

        financialDataWrapper.setAuxBalanceDataWrapper(auxBalanceDataWrapper);
        return financialDataWrapper;
    }

    private List<AssVo> getAssVoList(Set<String> hySet, Set<String> deptSet) {
        List<AssVo> res = new ArrayList<>();
        AssVo assVo = new AssVo();
        assVo.setChecktypecode("J04Ass");
        assVo.setCheckvaluecode(String.join(",", hySet));
        res.add(assVo);
        if (CollectionUtils.isNotEmpty(deptSet)) {
            AssVo assVo1 = new AssVo();
            assVo1.setChecktypecode("2");
            assVo1.setCheckvaluecode(String.join(",", deptSet));
            res.add(assVo1);
        }
        return res;
    }
}
