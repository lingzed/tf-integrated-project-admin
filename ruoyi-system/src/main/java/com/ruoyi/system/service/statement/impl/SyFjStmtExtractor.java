package com.ruoyi.system.service.statement.impl;

import com.ruoyi.common.enums.u8c.SubjDirection;
import com.ruoyi.common.u8c.AuxAcctProject;
import com.ruoyi.common.u8c.AuxBalance;
import com.ruoyi.common.u8c.Balance;
import com.ruoyi.common.u8c.SubjBalance;
import com.ruoyi.common.u8c.warpper.AuxBalanceDataWrapper;
import com.ruoyi.common.u8c.warpper.FinancialDataWrapper;
import com.ruoyi.common.u8c.warpper.SubjBalanceDataWrapper;
import com.ruoyi.system.domain.statement.CellWriter;
import com.ruoyi.system.domain.statement.RowColHeadIndex;
import com.ruoyi.system.domain.statement.StmtGenContext;
import com.ruoyi.system.domain.statement.cfg.RowColHeadIndexCfg;
import com.ruoyi.system.domain.statement.cfg.StmtCfgWrapper;
import com.ruoyi.system.service.statement.BaseStmtExtractor;
import com.ruoyi.system.service.statement.ValueExtractor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

/**
 * 损益分解表提起器
 */
@Component
public class SyFjStmtExtractor extends BaseStmtExtractor implements ValueExtractor<BigDecimal> {
    private static final Logger log = LoggerFactory.getLogger(SyFjStmtExtractor.class);

    @Override
    public List<CellWriter<BigDecimal>> extract(FinancialDataWrapper data, RowColHeadIndex rcHeadIndex,
                                                StmtCfgWrapper stmtCfg, Integer index, StmtGenContext context) {
        List<CellWriter<BigDecimal>> result = new ArrayList<>();

        AuxBalanceDataWrapper auxBalanceDataWrapper = data.getAuxBalanceDataWrapper();
        SubjBalanceDataWrapper subjBalanceDataWrapper = data.getSubjBalanceDataWrapper();

        RowColHeadIndexCfg indexCfg = Optional.ofNullable(stmtCfg)
                .map(StmtCfgWrapper::getListIndexCfg)
                .map(list -> list.get(index)).orElse(null);
        if (indexCfg == null) return result;
        List<Integer> rowHdColArea = indexCfg.getRowHeadCfgList().get(0).getRowHdColArea();
        Integer start = rowHdColArea.get(0);
        Integer end = rowHdColArea.get(1);
        int len = end - start + 1;

        processAbList(auxBalanceDataWrapper, rcHeadIndex, len, result);
        processSbList(subjBalanceDataWrapper, rcHeadIndex, len, result);
        return result;
    }

    private void processAbList(AuxBalanceDataWrapper auxBalanceDataWrapper, RowColHeadIndex rcHeadIndex,
                               Integer len, List<CellWriter<BigDecimal>> result) {
        if (auxBalanceDataWrapper == null) return;
        List<AuxBalance> abList = auxBalanceDataWrapper.getCurrPeriod();
        if (CollectionUtils.isEmpty(abList)) return;

        abList.forEach(ab -> {
            List<AuxAcctProject> aapList = ab.getGlqueryassvo();
            if (CollectionUtils.isEmpty(aapList)) return;

            String deptCode = aapList.get(0).getAsscode();  // 部门编码
            String subjCode = ab.getPk_accsubj_code();  // 科目编码

            int ri = rowIndex(rcHeadIndex, subjCode);
            int ci = colIndex(rcHeadIndex, deptCode);
            if (ri < 0 || ci < 0) return;
            if (subjCode.equals("221102") || subjCode.equals("221104")) {
//                BigDecimal init = initBalance(ab, subjCode);    // 期初数
                BigDecimal init = getInit(ab);    // 期初数
                addCellWriter(result, ri, ci, init);
                addCellWriter(result, ri + 1, ci, currBalance(ab, SubjDirection.DEBIT));    // 本期借方
                addCellWriter(result, ri + 2, ci, currBalance(ab, SubjDirection.CREDIT));   // 本期贷方
                addCellWriter(result, ri, ci + len, init);
                addCellWriter(result, ri + 1, ci + len, accumBalance(ab, SubjDirection.DEBIT));    // 累计借方
                addCellWriter(result, ri + 2, ci + len, accumBalance(ab, SubjDirection.CREDIT));   // 累计贷方
            } else {
                SubjDirection subjDirection = subjDirection(subjCode);
                BigDecimal bys = currBalance(ab, subjDirection);     // 本月数
                BigDecimal ljs = accumBalance(ab, subjDirection);    // 累计数
                addCellWriter(result, ri, ci, bys);
                addCellWriter(result, ri, ci + len, ljs);
            }
        });
    }

    private void processSbList(SubjBalanceDataWrapper subjBalanceDataWrapper, RowColHeadIndex rcHeadIndex,
                               Integer len, List<CellWriter<BigDecimal>> result) {
        if (subjBalanceDataWrapper == null) return;
        List<SubjBalance> sbList = subjBalanceDataWrapper.getCurrPeriod();
        if (CollectionUtils.isEmpty(sbList)) return;
        int ci = colIndex(rcHeadIndex, "科目余额");
        if (ci < 0) return;

        Map<String, Map<String, BigDecimal>> subjBalanceCount = new HashMap<>();
        rcHeadIndex.getColHeadIdx().keySet().forEach(subj -> sbList.forEach(sb -> {
            String subSubj = sb.getPk_accsubj_code();
            if (!subSubj.startsWith(subj)) return;
            Map<String, BigDecimal> balanceCount = subjBalanceCount.computeIfAbsent(subj, k -> new HashMap<>());
            if (subj.equals("221102") || subj.equals("221104")) {
//            mergeBigDecimal(balanceCount, "init", initBalance(sb, subSubj));    // 期初
                mergeBigDecimal(balanceCount, "init", getInit(sb));    // 期初
                mergeBigDecimal(balanceCount, "bqj", currBalance(sb, SubjDirection.DEBIT));    // 本期借
                mergeBigDecimal(balanceCount, "bqd", currBalance(sb, SubjDirection.CREDIT));    // 本期贷
                mergeBigDecimal(balanceCount, "ljj", accumBalance(sb, SubjDirection.DEBIT));    // 累计借
                mergeBigDecimal(balanceCount, "ljd", accumBalance(sb, SubjDirection.CREDIT));    // 累计贷
            }else{
                SubjDirection subjDirection = subjDirection(subj);
                mergeBigDecimal(balanceCount, "bys", currBalance(sb, subjDirection));    // 本月数
                mergeBigDecimal(balanceCount, "ljs", accumBalance(sb, subjDirection));    // 累计数
            }
        }));

        subjBalanceCount.forEach((subj, balanceCount) -> {
            int ri = rowIndex(rcHeadIndex, subj);
            if (ri < 0) return;

            if (subj.equals("221102") || subj.equals("221104")) {
                addCellWriter(result, ri, ci, balanceCount.get("init"));
                addCellWriter(result, ri + 1, ci, balanceCount.get("bqj"));    // 本期借方
                addCellWriter(result, ri + 2, ci, balanceCount.get("bqd"));   // 本期贷方
                addCellWriter(result, ri, ci + len, balanceCount.get("init"));
                addCellWriter(result, ri + 1, ci + len, balanceCount.get("ljj"));    // 累计借方
                addCellWriter(result, ri + 2, ci + len, balanceCount.get("ljd"));   // 累计贷方
            } else {
                addCellWriter(result, ri, ci, balanceCount.get("bys"));
                addCellWriter(result, ri, ci + len, balanceCount.get("ljs"));
            }
        });
    }

    // 221102和221104期初为贷-借
    private BigDecimal getInit(Balance balance) {
        BigDecimal qcj = balance.getInitDebitLocAmount();   // 期初借
        BigDecimal qcd = balance.getInitCreditLocAmount();  // 期初贷
        return bdSubtract(qcd, qcj);
    }
}
