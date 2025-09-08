package com.ruoyi.system.service.statement.impl;

import com.ruoyi.common.enums.u8c.SubjDirection;
import com.ruoyi.common.u8c.AuxAcctProject;
import com.ruoyi.common.u8c.AuxBalance;
import com.ruoyi.common.u8c.warpper.AuxBalanceDataWrapper;
import com.ruoyi.common.u8c.warpper.FinancialDataWrapper;
import com.ruoyi.system.domain.statement.CellWriter;
import com.ruoyi.system.domain.statement.RowColHeadIndex;
import com.ruoyi.system.domain.statement.StmtGenContext;
import com.ruoyi.system.domain.statement.cfg.StmtCfgWrapper;
import com.ruoyi.system.service.statement.BaseStmtExtractor;
import com.ruoyi.system.service.statement.ValueExtractor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 人工成本统计表提取器
 */
@Component
public class RgCbTjStmtExtractor extends BaseStmtExtractor implements ValueExtractor<BigDecimal> {
    @Override
    public List<CellWriter<BigDecimal>> extract(FinancialDataWrapper data, RowColHeadIndex rcHeadIndex,
                                                StmtCfgWrapper stmtCfg, Integer index, StmtGenContext context) {
        List<CellWriter<BigDecimal>> result = new ArrayList<>();

        AuxBalanceDataWrapper auxBalanceDataWrapper = data.getAuxBalanceDataWrapper();
        List<AuxBalance> curr = auxBalanceDataWrapper.getCurrPeriod();
        List<AuxBalance> first = auxBalanceDataWrapper.getPeriod1();
        // 首期
        byPeriod(first, rcHeadIndex, (ab, is1122, rowIndex, colIndex) -> {
            BigDecimal initBalance = getInitBalance(ab);    // 期初数
            addCellWriter(result, rowIndex, colIndex, initBalance);  // 设置期初
        });
        // 本期
        byPeriod(curr, rcHeadIndex, (ab, is1122, rowIndex, colIndex) -> {
            BigDecimal bqd = currBalance(ab, SubjDirection.CREDIT); // 本期贷方
            BigDecimal bqj = currBalance(ab, SubjDirection.DEBIT); // 本期借方
            BigDecimal ljd = accumBalance(ab, SubjDirection.CREDIT);   // 累计贷方
            BigDecimal ljj = accumBalance(ab, SubjDirection.DEBIT);   // 累计借方

            BigDecimal balanceD = is1122 ? ljd : bqd;  // 设置贷方数
            BigDecimal balanceJ = is1122 ? ljj : bqj;    // 设置借方数
            addCellWriter(result, rowIndex, colIndex + 1, balanceD);
            addCellWriter(result, rowIndex, colIndex + 2, balanceJ);
            if (!is1122) {
                addCellWriter(result, rowIndex, colIndex + 3, ljj);  // 设置累计借
            }
        });
        return result;
    }

    private void byPeriod(List<AuxBalance> abList, RowColHeadIndex rcHeadIndex, MyFunc myFunc) {
        abList.forEach(ab -> {
            List<AuxAcctProject> aapList = ab.getGlqueryassvo();
            if (CollectionUtils.isEmpty(aapList)) return;

            String subjCode = ab.getPk_accsubj_code();
            boolean is1122 = subjCode.startsWith("2211");
            String deptCode = aapList.get(0).getAsscode();

            int rowIndex = rowIndex(rcHeadIndex, subjCode);
            int colIndex = colIndex(rcHeadIndex, deptCode);
            if (rowIndex < 0 || colIndex < 0) return;

            myFunc.process(ab, is1122, rowIndex, colIndex);
        });
    }

    private BigDecimal getInitBalance(AuxBalance ab) {
        BigDecimal initCreditLocAmount = ab.getInitCreditLocAmount() == null
                ? BigDecimal.ZERO
                : ab.getInitCreditLocAmount();   // 期初贷
        BigDecimal initDebitLocAmount = ab.getInitDebitLocAmount() == null
                ? BigDecimal.ZERO
                : ab.getInitDebitLocAmount();   // 期初借
        return initCreditLocAmount.subtract(initDebitLocAmount);
    }

    interface MyFunc {
        void process(AuxBalance ab, Boolean is1122, Integer rowI, Integer colI);
    }
}
