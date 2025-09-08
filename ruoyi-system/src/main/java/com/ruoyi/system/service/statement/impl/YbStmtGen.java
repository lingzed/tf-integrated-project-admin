package com.ruoyi.system.service.statement.impl;

import com.ruoyi.common.enums.statement.StatementCfgType;
import com.ruoyi.common.u8c.AssVo;
import com.ruoyi.common.u8c.AuxBalance;
import com.ruoyi.common.u8c.SubjBalance;
import com.ruoyi.common.u8c.warpper.AuxBalanceDataWrapper;
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
 * 月报生成器
 */
@Component
public class YbStmtGen extends StatementGenProcess {
    private static final Map<String, QueryOfSheetKey> QUERY_HANDLE = new HashMap<>();
    private static final Logger log = LoggerFactory.getLogger(YbStmtGen.class);
    private static final String[] SHEET_KEYS = new String[]{
            "YJSF", // 应交税费
            "YJSF-WY", // 应交税费-物业
            "CWFY", // 财务费用
            "CH", // 存货
            "CQDTFYFLBD", // 长期待摊费用分类变动
            "CQGQTZ", // 长期股权投资
            "GDZCFLBD", // 固定资产分类变动
            "GLFY", // 管理费用
            "HTLYCB", // 合同履约成本
            "QTQYGJTZ", // 其他权益工具投资
            "QTYWSZ", // 其他业务收支
            "RGCB", // 人工成本
            "SYQZC", // 使用权资产
            "TZXFDCFLBD", // 投资性房地产分类变动
            "WXZCFLBD", // 无形资产分类变动
            "YYWSZ", // 营业外收支
            "YFZGXC",    // 应付职工薪酬
            "ZCCZSYJQTSY",    // 资产处置收益及其他收益
            "ZCJZSSJXYJZSS",    // 资产减值损失及信用减值损失
            "ZLFZ",    // 租赁负债
            "ZXCB",    // 专项储备
            "ZYYWCB",    // 主营业务成本
            "SJJFJ"    // 主营业务成本
    };
    @Resource
    private StatementCfgService statementCfgService;
    @Resource
    private StatementReadService statementReadService;
    @Resource
    private StatementWriteService statementWriteService;
    @Resource
    private YbStmtExtractor ybStmtExtractor;
    @Resource(name = "requestThreadPoolExecutor")
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    {
        for (int i = 0; i < SHEET_KEYS.length; i++) {
            String sheetKey = SHEET_KEYS[i];
            if (i == 0) {
                QUERY_HANDLE.put(sheetKey, this::queryOfYJSF);
            } else if (i == 1 || i == 16) { // 应交税费-物业 | 应付职工薪酬 不查询
                QUERY_HANDLE.put(sheetKey, null);
            } else if (i == 15) {
                QUERY_HANDLE.put(sheetKey, this::doubleSubjQuery);
            } else {
                QUERY_HANDLE.put(sheetKey, this::commonQueryBySubj);
            }
        }
    }

    @Override
    public String confirmStmtTplName(StmtGenContext context) {
        return "TPL_YB_" + context.corpCode().substring(0, 3);
    }

    @Override
    public StmtCfgWrapper loadStmtCfg(StmtGenContext context) {
        String indexKey = StmtRelatedUtil.getCfgCode(context.statementTpl(), StatementCfgType.ROW_COL_HEAD_INDEX_CFG);
        String writeKey = StmtRelatedUtil.getCfgCode(context.statementTpl(), StatementCfgType.SHEET_WRITE_CFG);
        String queryKey = StmtRelatedUtil.getCfgCode(context.statementTpl(), StatementCfgType.STATEMENT_QUERY_CFG);

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
        Map<String, FinancialDataWrapper> dataMap = new HashMap<>();

        Map<String, StatementQueryCfg> mapQueryCfg = stmtCfgWrapper.getMapQueryCfg();
        List<String> corpList = mapQueryCfg.get("commonCorpList").getQueryCorpList();   // 通用查询公司列表
        boolean isGt3 = corpList.size() > 3;
        String start = context.periodYear() + "-01";    // 开始期间
        String end = context.periodStr();    // 结束期间

        List<CompletableFuture<Void>> futures = new ArrayList<>();
        // 按sheet遍历
        int sheetNum = 1;
        for (Map.Entry<String, RowColHeadIndex> entry : rcHeadIndexMap.entrySet()) {
            String sheetKey = entry.getKey();
            RowColHeadIndex rcHeadIndex = entry.getValue();
            FinancialDataWrapper dataWrapper = dataMap.computeIfAbsent(sheetKey, k -> new FinancialDataWrapper());
            QueryOfSheetKey queryOfSheetKey = QUERY_HANDLE.get(sheetKey);
            if (queryOfSheetKey == null) continue;
            // 执行请求任务
            queryOfSheetKey.query(rcHeadIndex, mapQueryCfg.get(sheetKey), dataWrapper, futures, corpList, start, end, context);

            if (isGt3) {
                // 每遍历一个sheet就等待这个sheet内部的请求任务完成
                waitComplete(futures);
            } else {
                // 每3个sheet遍历完才等待请求任务完成
                if (sheetNum == 3) {
                    waitComplete(futures);
                    sheetNum = 1;
                } else {
                    sheetNum++;
                }
            }
        }

        if (!isGt3 && !futures.isEmpty()) {
            waitComplete(futures);
        }

        // 人工成本的查询结果保存到应付职工薪酬
        dataMap.put(SHEET_KEYS[16], dataMap.get(SHEET_KEYS[11]));
        return dataMap;
    }

    private void waitComplete(List<CompletableFuture<Void>> futures) {
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[]{})).join();
        futures.clear();
    }

    @Override
    public byte[] writeData(File stmtTplFile, Map<String, RowColHeadIndex> rcHeadIndexMap, Map<String, FinancialDataWrapper> dataMap, StmtCfgWrapper stmtCfg, StmtGenContext context) throws IOException {
        return statementWriteService.coordinateWriter(stmtTplFile, dataMap, rcHeadIndexMap, stmtCfg, context, ybStmtExtractor);
    }

    // 通过sheetKey
    interface QueryOfSheetKey {
        void query(RowColHeadIndex rcHeadIndex, StatementQueryCfg queryCfg, FinancialDataWrapper dataWrapper, List<CompletableFuture<Void>> futures, List<String> corpList, String start, String end, StmtGenContext context);
    }

    private List<AssVo> getAssVoList(String... typeCode) {
        return Stream.of(typeCode).map(code -> {
            AssVo assVo = new AssVo();
            assVo.setChecktypecode(code);
            return assVo;
        }).collect(Collectors.toList());
    }

    // 应交税费查询
    private void queryOfYJSF(RowColHeadIndex rcHeadIndex, StatementQueryCfg queryCfg, FinancialDataWrapper dataWrapper, List<CompletableFuture<Void>> futures, List<String> corpList, String start, String end, StmtGenContext context) {
        Set<String> subjSet = queryCfg.getQuerySubjList();
        String from = queryCfg.getSubjCodeFrom();
        String to = queryCfg.getSubjCodeTo();
        SubjBalanceDataWrapper subjBalanceDataWrapper = new SubjBalanceDataWrapper();
        AuxBalanceDataWrapper auxBalanceDataWrapper = new AuxBalanceDataWrapper();
        dataWrapper.setSubjBalanceDataWrapper(subjBalanceDataWrapper);
        dataWrapper.setAuxBalanceDataWrapper(auxBalanceDataWrapper);

        corpList.forEach(corp -> {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                // 科目查询
                List<SubjBalance> sbList = U8CApiUtil.subjBalanceQuery(corp, start, end, from, to);
                if (CollectionUtils.isNotEmpty(sbList)) {
                    subjBalanceDataWrapper.setByCorpCode(corp, sbList);
                }
                // 辅助余额查询
                List<AuxBalance> abList = U8CApiUtil.queryAuxBalance(corp, start, end, subjSet, getAssVoList("J01Ass"));
                if (CollectionUtils.isEmpty(abList)) return;
                auxBalanceDataWrapper.setByCorpCode(corp, abList);
            }, threadPoolTaskExecutor);
            futures.add(future);
        });
    }

    /*
     * 通用科目余额查询
     * 按公司遍历，起始科目进行查询
     * */
    private void commonQueryBySubj(RowColHeadIndex rcHeadIndex, StatementQueryCfg queryCfg, FinancialDataWrapper dataWrapper, List<CompletableFuture<Void>> futures, List<String> corpList, String start, String end, StmtGenContext context) {
        SubjBalanceDataWrapper subjBalanceDataWrapper = setSbWrapper(dataWrapper);

        String from = queryCfg.getSubjCodeFrom();
        String to = queryCfg.getSubjCodeTo();
        corpList.forEach(corp -> {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                List<SubjBalance> sbList = U8CApiUtil.subjBalanceQuery(corp, start, end, from, to);
                if (CollectionUtils.isEmpty(sbList)) return;
                subjBalanceDataWrapper.setByCorpCode(corp, sbList);
            }, threadPoolTaskExecutor);
            futures.add(future);
        });
    }

    /*
     * 两个科目分别查询
     * */
    private void doubleSubjQuery(RowColHeadIndex rcHeadIndex, StatementQueryCfg queryCfg, FinancialDataWrapper dataWrapper, List<CompletableFuture<Void>> futures, List<String> corpList, String start, String end, StmtGenContext context) {
        SubjBalanceDataWrapper subjBalanceDataWrapper = setSbWrapper(dataWrapper);

        String from = queryCfg.getSubjCodeFrom();
        String to = queryCfg.getSubjCodeTo();
        corpList.forEach(corp -> {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                List<SubjBalance> sbList1 = U8CApiUtil.subjBalanceQuery(corp, start, end, from, from);
                List<SubjBalance> sbList2 = U8CApiUtil.subjBalanceQuery(corp, start, end, to, to);
                if (CollectionUtils.isEmpty(sbList1) && CollectionUtils.isEmpty(sbList2)) return;
                sbList1.addAll(sbList2);
                subjBalanceDataWrapper.setByCorpCode(corp, sbList1);
            }, threadPoolTaskExecutor);
            futures.add(future);
        });
    }

    private SubjBalanceDataWrapper setSbWrapper(FinancialDataWrapper dataWrapper) {
        SubjBalanceDataWrapper subjBalanceDataWrapper = new SubjBalanceDataWrapper();
        dataWrapper.setSubjBalanceDataWrapper(subjBalanceDataWrapper);
        return subjBalanceDataWrapper;
    }
}
