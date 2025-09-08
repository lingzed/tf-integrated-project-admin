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
import com.ruoyi.system.domain.statement.cfg.*;
import com.ruoyi.system.service.StatementCfgService;
import com.ruoyi.system.service.statement.StatementReadService;
import com.ruoyi.system.service.statement.StatementWriteService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.SetUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 应收账款回款率生成器
 */
@Component
public class YsZkHklStmtGen extends StatementGenProcess {
    @Resource
    private StatementCfgService statementCfgService;
    @Resource
    private StatementReadService statementReadService;
    @Resource
    private StatementWriteService statementWriteService;
    @Resource
    private YsZkHklStmtExtractor ysZkHklStmtExtractor;

    @Override
    public String confirmStmtTplName(StmtGenContext context) {
        return "TPL_YS_ZK_HKL_" + context.corpCode();
    }

    @Override
    public StmtCfgWrapper loadStmtCfg(StmtGenContext context) {
        StatementTpl statementTpl = context.statementTpl();
        String indexKey = StmtRelatedUtil.getCfgCode(statementTpl, StatementCfgType.ROW_COL_HEAD_INDEX_CFG);
        String writeKey = StmtRelatedUtil.getCfgCode(statementTpl, StatementCfgType.SHEET_WRITE_CFG);
        String dataKey = StmtRelatedUtil.getCfgCode(statementTpl, StatementCfgType.ROW_COL_HEAD_DATA_MAPPER_CFG);

        List<RowColHeadIndexCfg> indexCfg = statementCfgService.getListStmtCfgCache(indexKey, RowColHeadIndexCfg.class);
        List<SheetWriteCfg> writeCfg = statementCfgService.getListStmtCfgCache(writeKey, SheetWriteCfg.class);
        RowColHeadDataMapperCfg dataCfg = statementCfgService.getObjStmtCfgCache(dataKey, RowColHeadDataMapperCfg.class);

        StmtCfgWrapper stmtCfgWrapper = new StmtCfgWrapper();
        stmtCfgWrapper.setListIndexCfg(indexCfg);
        stmtCfgWrapper.setListWriteCfg(writeCfg);
        stmtCfgWrapper.setObjDataCfg(dataCfg);
        return stmtCfgWrapper;
    }

    @Override
    public Map<String, RowColHeadIndex> getRowColHeadIndexMap(File stmtTplFile, StmtCfgWrapper stmtCfgWrapper, StmtGenContext context) throws IOException {
        return statementReadService.extractRowColHead(stmtTplFile, stmtCfgWrapper.getListIndexCfg());
    }

    @Override
    public Boolean withDataTplHandle(StmtGenContext context) {
        return true;
    }

    @Override
    public String getStmtDataTplFilepath(StmtGenContext context) {
        String dataTplFile = super.getStmtDataTplFilepath(context);
        return String.format(dataTplFile, "数据模板");
    }

    @Override
    public Map<String, Map<String, String>> getRowColHeadMapData(File dataTplFile, StmtCfgWrapper stmtCfgWrapper, StmtGenContext context) throws IOException {
        return statementReadService.getRowColHeadMapData(dataTplFile, stmtCfgWrapper.getObjDataCfg());
    }

    @Override
    public Map<String, FinancialDataWrapper> fetchData(Map<String, RowColHeadIndex> rcHeadIndexMap, StmtCfgWrapper stmtCfgWrapper, StmtGenContext context) {
        Map<String, FinancialDataWrapper> result = new HashMap<>();
        String corpCode = context.corpCode();
        FinancialDataWrapper dataWrapper = result.computeIfAbsent(corpCode, k -> new FinancialDataWrapper());
        AuxBalanceDataWrapper auxBalanceDataWrapper = new AuxBalanceDataWrapper();
        dataWrapper.setAuxBalanceDataWrapper(auxBalanceDataWrapper);

        Set<String> subjSet = SetUtils.hashSet("1122", "2203");
        List<AssVo> assVos = assVoList();
        YearMonth period = context.period();    // 本期
        int currYear = context.periodYear();    // 本年

        String first = PeriodUtil.firstPeriod(period);  // 首期
        mergeResult(dataWrapper, U8CApiUtil.queryAuxBalance(corpCode, first, context.periodStr(), subjSet, assVos), false);

        if (currYear > 2025) {
            String lastPeriod = PeriodUtil.lastYearPeriod(period);  // 上年同期
            String lastFirst = PeriodUtil.firstPeriod(lastPeriod);  // 上年首期
            mergeResult(dataWrapper, U8CApiUtil.queryAuxBalance(corpCode, lastFirst, lastPeriod, subjSet, assVos), true);

            // 上年，从2026开始
            last1to12(dataWrapper, corpCode, currYear, subjSet, assVos, false);
            // 上上年从2027开始
            if (currYear > 2026) {
                last1to12(dataWrapper, corpCode, currYear, subjSet, assVos, true);
            }
        }
        return result;
    }

    @Override
    public byte[] writeData(File stmtTplFile, Map<String, RowColHeadIndex> rcHeadIndexMap, Map<String, FinancialDataWrapper> dataMap, StmtCfgWrapper stmtCfg, StmtGenContext context) throws IOException {
        return statementWriteService.coordinateWriter(stmtTplFile, dataMap, rcHeadIndexMap, stmtCfg, context, ysZkHklStmtExtractor);
    }

    private List<AssVo> assVoList() {
        return Stream.of("2").map(e -> {
            AssVo assVo = new AssVo();
            assVo.setChecktypecode(e);
            return assVo;
        }).collect(Collectors.toList());
    }

    private void mergeResult(FinancialDataWrapper dataWrapper, List<AuxBalance> list, Boolean isPre) {
        if (CollectionUtils.isEmpty(list)) return;
        AuxBalanceDataWrapper auxBalanceDataWrapper = dataWrapper.getAuxBalanceDataWrapper();
        if (isPre) {
            List<AuxBalance> preYearFirstToCurr = auxBalanceDataWrapper.getPreYearFirstToCurr();
            if (CollectionUtils.isEmpty(preYearFirstToCurr)) {
                auxBalanceDataWrapper.setPreYearFirstToCurr(list);
            } else {
                preYearFirstToCurr.addAll(list);
            }
        } else {
            List<AuxBalance> firstPeriodToCurr = auxBalanceDataWrapper.getFirstPeriodToCurr();
            if (CollectionUtils.isEmpty(firstPeriodToCurr)) {
                auxBalanceDataWrapper.setFirstPeriodToCurr(list);
            } else {
                firstPeriodToCurr.addAll(list);
            }
        }
    }

    private void last1to12(FinancialDataWrapper dataWrapper, String corpCode, int currYear, Set<String> subjSet,
                           List<AssVo> assVoList, boolean isPPre) {
        int lastYear = isPPre ? currYear - 2 : currYear - 1;    // 上上年 | 上年
        List<AuxBalance> lyList = U8CApiUtil.queryAuxBalance(corpCode, lastYear + "-01", lastYear + "-12", subjSet, assVoList);
        if (CollectionUtils.isEmpty(lyList)) return;
        AuxBalanceDataWrapper auxBalanceDataWrapper = dataWrapper.getAuxBalanceDataWrapper();
        if (isPPre) {
            // 上上年1-12月的辅助余额列表
            auxBalanceDataWrapper.setPPreYear(lyList);
        } else {
            // 上年1-12月的辅助余额列表
            auxBalanceDataWrapper.setPreYear(lyList);
        }
    }
}
