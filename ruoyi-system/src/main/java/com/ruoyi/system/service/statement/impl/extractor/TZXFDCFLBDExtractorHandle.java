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
 * 投资性房地产分类变动sheet提取逻辑
 */
//@Component("TZXFDCFLBD")
public class TZXFDCFLBDExtractorHandle extends BaseStmtExtractor implements ExtractorHandle {
    private static final String[] colKeys = new String[]{
            "一、年初数", "三、本年提取(摊销)", "六、期末数"
    };

    @Override
    public void handle(List<CellWriter<Object>> result, FinancialDataWrapper data, RowColHeadIndex rcHeadIndex,
                       List<String> corpList, StmtGenContext context) {
        Map<String, Map<String, BigDecimal>> bCountMap = new HashMap<>();
        Set<String> subjSet = rcHeadIndex.getRowHeadIdx().keySet();
        SubjBalanceDataWrapper subjBalanceDataWrapper = data.getSubjBalanceDataWrapper();

        corpList.forEach(corp -> {
            List<SubjBalance> sbList = subjBalanceDataWrapper.getByCorpCode(corp);
            if (CollectionUtils.isEmpty(sbList)) return;

            subjSet.forEach(pSubj -> sbList.forEach(sb -> {
                String subjCode = sb.getPk_accsubj_code();
                if (!subjCode.startsWith(pSubj)) return;

                SubjDirection subjDirection = subjDirection(subjCode);
                BigDecimal init = subtractInitBalance(sb, subjDirection); // 期初
                BigDecimal bq = currBalance(sb, subjDirection); // 本期
                BigDecimal end = subtractEndBalance(sb, subjDirection);    // 期末
                BigDecimal[] bArr = new BigDecimal[]{init, bq, end};

                for (int i = 0; i < colKeys.length; i++) {
                    mergeBigDecimal(bCountMap.computeIfAbsent(colKeys[i], k -> new HashMap<>()), pSubj, bArr[i]);
                }
            }));

            bCountMap.forEach((rowKey, row) -> {
                int ri = rowIndex(rcHeadIndex, rowKey);
                row.forEach((colKey, val) -> {
                    int ci = colIndex(rcHeadIndex, colKey);
                    addCellWriter(result, ri, ci, val);
                });
            });
        });
    }
}
