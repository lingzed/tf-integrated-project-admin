package com.ruoyi.system.service.statement.impl.extractor;

import com.ruoyi.common.enums.u8c.SubjDirection;
import com.ruoyi.common.u8c.SubjBalance;
import com.ruoyi.common.u8c.warpper.FinancialDataWrapper;
import com.ruoyi.common.u8c.warpper.SubjBalanceDataWrapper;
import com.ruoyi.system.domain.statement.CellWriter;
import com.ruoyi.system.domain.statement.RowColHeadIndex;
import com.ruoyi.system.domain.statement.StmtGenContext;
import com.ruoyi.system.service.statement.BaseStmtExtractor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 合同履约成本sheet提取逻辑
 */
@Component("HTLYCB")
public class HTLYCBExtractorHandle extends BaseStmtExtractor implements ExtractorHandle {
    private static final String colKey = "原值";

    @Override
    public void handle(List<CellWriter<Object>> result, FinancialDataWrapper data, RowColHeadIndex rcHeadIndex,
                       List<String> corpList, StmtGenContext context) {
        SubjBalanceDataWrapper subjBalanceDataWrapper = data.getSubjBalanceDataWrapper();
        Set<String> subjSet = rcHeadIndex.getColHeadIdx().keySet();
        Map<String, Map<String, BigDecimal>> bCountMap = new HashMap<>();

        corpList.forEach(corp -> {
            List<SubjBalance> sbList = subjBalanceDataWrapper.getByCorpCode(corp);
            if (CollectionUtils.isEmpty(sbList)) return;

            subjSet.forEach(pSubj -> sbList.forEach(sb -> {
                String subjCode = sb.getPk_accsubj_code();
                if (!subjCode.startsWith(pSubj)) return;
                SubjDirection subjDirection = subjDirection(subjCode);
//                BigDecimal end = endBalance(sb, subjDirection);   // 期末数
                BigDecimal end = subtractEndBalance(sb, subjDirection);   // 期末数
                Map<String, BigDecimal> aRow = bCountMap.computeIfAbsent(pSubj, k -> new HashMap<>());
                mergeBigDecimal(aRow, colKey, end);
            }));
        });

        bCountMap.forEach((rowKey, row) -> {
            int ri = rowIndex(rcHeadIndex, rowKey);
            row.forEach((colKey, val) -> {
                int ci = colIndex(rcHeadIndex, colKey);
                addCellWriter(result, ri, ci, val);
            });
        });
    }
}
