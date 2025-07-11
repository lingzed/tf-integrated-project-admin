package com.ruoyi.system.service.statement.impl;

import com.ruoyi.common.enums.statement.StatementCfgType;
import com.ruoyi.common.enums.statement.StatementTpl;
import com.ruoyi.common.u8c.AssVo;
import com.ruoyi.common.u8c.AuxBalance;
import com.ruoyi.system.domain.statement.StmtGenContext;
import com.ruoyi.common.u8c.warpper.AuxBalanceDataWrapper;
import com.ruoyi.common.u8c.warpper.FinancialDataWrapper;
import com.ruoyi.common.utils.StmtRelatedUtil;
import com.ruoyi.common.utils.U8CApiUtil;
import com.ruoyi.system.domain.statement.RowColHeadIndex;
import com.ruoyi.system.domain.statement.cfg.RowColHeadIndexCfg;
import com.ruoyi.system.domain.statement.cfg.SheetWriteCfg;
import com.ruoyi.system.domain.statement.cfg.StatementQueryCfg;
import com.ruoyi.system.domain.statement.cfg.StmtCfgWrapper;
import com.ruoyi.system.service.StatementCfgService;
import com.ruoyi.system.service.statement.StatementReadService;
import com.ruoyi.system.service.statement.StatementWriteService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 成本结转表生成
 */
@Component
public class CbJzStmtGen extends StatementGenProcess {
    @Resource
    private StatementReadService statementReadService;
    @Resource
    private StatementWriteService statementWriteService;
    @Resource
    private StatementCfgService statementCfgService;
    @Resource
    private CbJzStmtExtractor cbJzStmtExtractor;

    @Override
    public String confirmStmtTplName(StmtGenContext context) {
        return "TPL_CB_JZB_" + context.corpCode();
    }

    @Override
    public StmtCfgWrapper loadStmtCfg(StmtGenContext context) {
        StatementTpl statementTpl = context.statementTpl();
        // 获取配置项缓存key
        String indexCfgKey = StmtRelatedUtil.getCfgCode(statementTpl, StatementCfgType.ROW_COL_HEAD_INDEX_CFG);
        String writeCfgKey = StmtRelatedUtil.getCfgCode(statementTpl, StatementCfgType.SHEET_WRITE_CFG);
        String queryCfgKey = StmtRelatedUtil.getCfgCode(statementTpl, StatementCfgType.STATEMENT_QUERY_CFG);

        // 获取配置项
        List<RowColHeadIndexCfg> indexCfg = statementCfgService.getListStmtCfgCache(indexCfgKey, RowColHeadIndexCfg.class);
        List<SheetWriteCfg> writeCfg = statementCfgService.getListStmtCfgCache(writeCfgKey, SheetWriteCfg.class);
        StatementQueryCfg queryCfg = statementCfgService.getObjStmtCfgCache(queryCfgKey, StatementQueryCfg.class);

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

        List<String> remove = stmtCfgWrapper.getObjQueryCfg().getRowHdQueryRemove();
        List<AssVo> assVoList = getAssVoList(false);
        String period = context.periodStr();
        String corpCode = context.corpCode();

        RowColHeadIndex rcHead = rcHeadIndexMap.get(corpCode);
        if (rcHead == null) {
            throw new RuntimeException(String.format("公司【%s】没有对应的行列头索引映射", corpCode));
        }
        Set<String> subjSet = rcHead.getRowHeadIdx().keySet()
                .stream().filter(subj -> !remove.contains(subj)).collect(Collectors.toSet());
        List<AuxBalance> auxBalances = U8CApiUtil.queryAuxBalance(corpCode, period, period, subjSet, assVoList);

        FinancialDataWrapper financialDataWrapper = new FinancialDataWrapper();
        AuxBalanceDataWrapper auxBalanceDataWrapper = new AuxBalanceDataWrapper();
        auxBalanceDataWrapper.setCurrPeriod(auxBalances);
        financialDataWrapper.setAuxBalanceDataWrapper(auxBalanceDataWrapper);
        resultMap.put(corpCode, financialDataWrapper);

        return resultMap;
    }

    @Override
    public byte[] writeData(File stmtTplFile, Map<String, RowColHeadIndex> rcHeadIndexMap, Map<String, FinancialDataWrapper> dataMap, StmtCfgWrapper stmtCfg, StmtGenContext context) throws IOException {
        return statementWriteService.coordinateWriter(stmtTplFile, dataMap, rcHeadIndexMap, stmtCfg, context, cbJzStmtExtractor);
    }

    private static List<AssVo> getAssVoList(boolean notCost) {
        return Stream.of(notCost ? new String[]{"2", "J06Ass"} : new String[]{"2", "J06Ass", "73"}).map(s -> {
            AssVo assVo = new AssVo();
            assVo.setChecktypecode(s);
            return assVo;
        }).collect(Collectors.toList());
    }
}
