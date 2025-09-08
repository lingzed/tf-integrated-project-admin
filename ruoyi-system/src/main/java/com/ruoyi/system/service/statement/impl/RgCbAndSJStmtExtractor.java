package com.ruoyi.system.service.statement.impl;

import com.ruoyi.common.enums.u8c.SubjDirection;
import com.ruoyi.common.u8c.SubjBalance;
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
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class RgCbAndSJStmtExtractor extends BaseStmtExtractor implements ValueExtractor<BigDecimal> {
    private static final String LJJ = "ljj";    // 累计借
    private static final String LJD = "ljd";    // 累计贷

    @Override
    public List<CellWriter<BigDecimal>> extract(FinancialDataWrapper data, RowColHeadIndex rcHeadIndex,
                                                StmtCfgWrapper stmtCfg, Integer index, StmtGenContext context) {
        List<CellWriter<BigDecimal>> result = new ArrayList<>();
        int ljjI = colIndex(rcHeadIndex, LJJ);    // 累计借
        int ljdI = colIndex(rcHeadIndex, LJD);    // 累计贷
        if (ljjI < 0 || ljdI < 0) return result;

        List<SubjBalance> sbList = data.getSubjBalanceDataWrapper().getCurrPeriod();
        if (CollectionUtils.isEmpty(sbList)) return result;

        Map<String, List<BigDecimal>> subjBalanceMap = rcHeadIndex.getColHeadIdx().keySet().stream()
                .collect(Collectors.toMap(k -> k, k -> {
                    List<BigDecimal> balanceList = new ArrayList<>();
                    for (int i = 0; i < 2; i++) {
                        balanceList.add(BigDecimal.ZERO);
                    }
                    return balanceList;
                }));   // 科目对应数

        for (Map.Entry<String, List<BigDecimal>> entry : subjBalanceMap.entrySet()) {
            String subjCode = entry.getKey();   // 科目id
            List<BigDecimal> balanceList = entry.getValue();   // 余额列表
            sbList.forEach(sb -> {
                String sCode = sb.getPk_accsubj_code();
                if (!sCode.startsWith(subjCode)) return;

                BigDecimal ljj = accumBalance(sb, SubjDirection.DEBIT); // 累计借
                BigDecimal ljd = accumBalance(sb,SubjDirection.CREDIT); // 累计贷
                if (ljj != null) {
                    balanceList.set(0, balanceList.get(0).add(ljj));
                }
                if (ljd != null) {
                    balanceList.set(1, balanceList.get(1).add(ljd));
                }
            });
        }

        subjBalanceMap.forEach((k, v) -> {
            int rowI = rowIndex(rcHeadIndex, k);
            if (rowI < 0) return;
            BigDecimal ljj = v.get(0);
            BigDecimal ljd = v.get(1);
            addCellWriter(result, rowI, ljjI, ljj);
            addCellWriter(result, rowI, ljdI, ljd);
        });
        return result;
    }
}
