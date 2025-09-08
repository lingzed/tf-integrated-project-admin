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
 * 应付职工薪酬sheet提取逻辑
 */
@Component("YFZGXC")
public class YFZGXCExtractorHandle extends BaseStmtExtractor implements ExtractorHandle {
    private static final String[] ROW_KEYS = new String[]{
            "年初余额", "本期增加", "期末余额"
    };

    @Override
    public void handle(List<CellWriter<Object>> result, FinancialDataWrapper data, RowColHeadIndex rcHeadIndex,
                       List<String> corpList, StmtGenContext context) {
        Map<String, String> colHdMap = new HashMap<>();
        Map<String, Map<String, BigDecimal>> bCountMap = new HashMap<>();

        rcHeadIndex.getColHeadIdx().keySet().forEach(colKey -> {
            bCountMap.computeIfAbsent(colKey, k -> new HashMap<>());
            if (colKey.contains("+")) {
                String[] colKeySplit = colKey.split("\\+");
                for (String subKey : colKeySplit) {
                    colHdMap.put(subKey, colKey);
                }
            } else {
                colHdMap.put(colKey, colKey);
            }
        });

        Set<String> subjSet = colHdMap.keySet();
        SubjBalanceDataWrapper subjBalanceDataWrapper = data.getSubjBalanceDataWrapper();
        corpList.forEach(corp -> {
            List<SubjBalance> sbList = subjBalanceDataWrapper.getByCorpCode(corp);
            if (CollectionUtils.isEmpty(sbList)) return;

            sbList.forEach(sb -> {
                String pCode = findPCode(sb.getPk_accsubj_code(), subjSet);
                if (pCode == null) return;
                Map<String, BigDecimal> aRow = bCountMap.get(colHdMap.get(pCode));
                if (aRow == null) return;
                SubjDirection subjDirection = subjDirection(pCode);
                BigDecimal init = subtractInitBalance(sb, subjDirection);   // 期初
                BigDecimal bq = currBalance(sb, subjDirection);             // 本期
                BigDecimal end = subtractEndBalance(sb, subjDirection);     // 期末
                BigDecimal[] bArr = new BigDecimal[]{init, bq, end};
                for (int i = 0; i < ROW_KEYS.length; i++) {
                    mergeBigDecimal(aRow, ROW_KEYS[i], bArr[i]);
                }
            });
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
