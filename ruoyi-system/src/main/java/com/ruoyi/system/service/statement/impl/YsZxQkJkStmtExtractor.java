package com.ruoyi.system.service.statement.impl;

import com.ruoyi.common.enums.u8c.SubjDirection;
import com.ruoyi.common.u8c.AuxAcctProject;
import com.ruoyi.common.u8c.AuxBalance;
import com.ruoyi.common.u8c.SubjBalance;
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
import java.util.*;

/**
 * 预算执行情况监控表提取器
 */
@Component
public class YsZxQkJkStmtExtractor extends BaseStmtExtractor implements ValueExtractor<BigDecimal> {
    @Override
    public List<CellWriter<BigDecimal>> extract(FinancialDataWrapper data, RowColHeadIndex rcHeadIndex,
                                                StmtCfgWrapper stmtCfg, Integer index, StmtGenContext context) {
        List<CellWriter<BigDecimal>> result = new ArrayList<>();
        Map<String, BigDecimal> subjMerge1 = new HashMap<>();
        Map<String, BigDecimal> subjMerge2 = new HashMap<>();

        int kyColi = colIndex(rcHeadIndex, "科目余额");
        if (kyColi < 0) return result;

        List<SubjBalance> sbList = data.getSubjBalanceDataWrapper().getCurrPeriod();
        rcHeadIndex.getColHeadIdx().keySet().forEach(subjCode -> {
            if (CollectionUtils.isEmpty(sbList)) return;
            sbList.forEach(sb -> {
                if (!sb.getPk_accsubj_code().startsWith(subjCode)) return;
                BigDecimal bqj = currBalance(sb, SubjDirection.DEBIT);   // 本期借
                BigDecimal bqd = currBalance(sb, SubjDirection.CREDIT);  // 本期贷
                mergeBigDecimal(subjMerge1, subjCode, bqj);
                mergeBigDecimal(subjMerge2, subjCode, bqd);
            });
        });

        List<AuxBalance> abList = Optional.ofNullable(data.getAuxBalanceDataWrapper())
                .map(AuxBalanceDataWrapper::getCurrPeriod)
                .orElse(Collections.emptyList());

        abList.forEach(ab -> {
            List<AuxAcctProject> aapList = ab.getGlqueryassvo();
            if (CollectionUtils.isEmpty(aapList)) return;
            String deptCode = aapList.get(0).getAsscode(); // 部门编码
            int coli = colIndex(rcHeadIndex, deptCode);
            String subj = ab.getPk_accsubj_code();
            int rowI = rowIndex(rcHeadIndex, subj);
            if (coli < 0 || rowI < 0) return;

            BigDecimal bqj = currBalance(ab, SubjDirection.DEBIT);   // 本期借
            BigDecimal bqd = currBalance(ab, SubjDirection.CREDIT);  // 本期贷
            addCellWriter(result, rowI, coli, bqj);
            addCellWriter(result, rowI, coli + 1, bqd);
        });

        subjMerge1.forEach((k, v) -> {
            int rowIndex = rowIndex(rcHeadIndex, k);
            if (rowIndex < 0) return;

            addCellWriter(result, rowIndex, kyColi, v);
            addCellWriter(result, rowIndex, kyColi + 1, subjMerge2.get(k));
        });
        return result;
    }
}
