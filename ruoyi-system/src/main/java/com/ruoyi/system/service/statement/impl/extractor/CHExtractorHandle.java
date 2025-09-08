package com.ruoyi.system.service.statement.impl.extractor;

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
 * 存货sheet提取逻辑
 */
@Component("CH")
public class CHExtractorHandle extends BaseStmtExtractor implements ExtractorHandle {
    private static final String QT = "其他";  // 列头

    @Override
    public void handle(List<CellWriter<Object>> result, FinancialDataWrapper data, RowColHeadIndex rcHeadIndex,
                       List<String> corpList, StmtGenContext context) {
        SubjBalanceDataWrapper subjBalanceDataWrapper = data.getSubjBalanceDataWrapper();
        Set<String> subjSet = rcHeadIndex.getRowHeadIdx().keySet(); // 科目编码
        Map<String, Map<String, BigDecimal>> bCountMap = new HashMap<>();
        Map<String, BigDecimal> aRow = bCountMap.computeIfAbsent(QT, k -> new HashMap<>());

        corpList.forEach(corp -> {
            List<SubjBalance> sbList = subjBalanceDataWrapper.getByCorpCode(corp);
            if (CollectionUtils.isEmpty(sbList)) return;

            subjSet.forEach(pSubj -> sbList.forEach(sb -> {
                String subj = sb.getPk_accsubj_code();
                if (!subj.startsWith(pSubj)) return;
                BigDecimal end = subtractEndBalance(sb, subj);    // 期末数
                mergeBigDecimal(aRow, pSubj, end);
            }));
        });

        bCountMap.forEach((rowKey, row) -> {
            int ri = rowIndex(rcHeadIndex, rowKey);
            row.forEach((subj, v) -> {
                int ci = colIndex(rcHeadIndex, subj);
                addCellWriter(result, ri, ci, v);
            });
        });
    }
}
