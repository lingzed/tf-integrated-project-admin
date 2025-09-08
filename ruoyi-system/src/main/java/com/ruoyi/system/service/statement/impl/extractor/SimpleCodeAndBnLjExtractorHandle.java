package com.ruoyi.system.service.statement.impl.extractor;

import com.ruoyi.common.u8c.SubjBalance;
import com.ruoyi.common.u8c.warpper.FinancialDataWrapper;
import com.ruoyi.common.u8c.warpper.SubjBalanceDataWrapper;
import com.ruoyi.system.domain.statement.CellWriter;
import com.ruoyi.system.domain.statement.RowColHeadIndex;
import com.ruoyi.system.domain.statement.StmtGenContext;
import com.ruoyi.system.service.statement.BaseStmtExtractor;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 列头只有简单科目(每个头只有一个科目，不包含其他科目、“+”、“，”)
 * 行头只有本年累计(取的是本期数)
 */
public class SimpleCodeAndBnLjExtractorHandle extends BaseStmtExtractor implements ExtractorHandle {
    private static final String ROW_KEY = "本年累计";
    private static final Logger log = LoggerFactory.getLogger(SimpleCodeAndBnLjExtractorHandle.class);

    @Override
    public void handle(List<CellWriter<Object>> result, FinancialDataWrapper data,
                       RowColHeadIndex rcHeadIndex, List<String> corpList, StmtGenContext context) {
        Map<String, Map<String, BigDecimal>> bCountMap = new HashMap<>();
        Map<String, String> colHdMap = new HashMap<>();
        rcHeadIndex.getColHeadIdx().keySet().forEach(code -> {
            bCountMap.computeIfAbsent(code, k -> new HashMap<>());
            addColHdMap(colHdMap, code);
        });
        Set<String> subjSet = colHdMap.isEmpty()? bCountMap.keySet() : colHdMap.keySet();

        SubjBalanceDataWrapper subjBalanceDataWrapper = data.getSubjBalanceDataWrapper();
        corpList.forEach(corp -> {
            List<SubjBalance> sbList = subjBalanceDataWrapper.getByCorpCode(corp);
            if (CollectionUtils.isEmpty(sbList)) return;

            sbList.forEach(sb -> {
                String pCode = findPCode(sb.getPk_accsubj_code(), subjSet);
                String key = colHdMap.isEmpty() ? pCode : colHdMap.get(pCode);
                if (key == null) return;
                Map<String, BigDecimal> aRow = bCountMap.get(key);
                if (aRow == null) return;

                BigDecimal bq = currBalance(sb, subjDirection(pCode));  // 本期
                mergeBigDecimal(aRow, ROW_KEY, bq);
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

    protected void addColHdMap(Map<String, String> colHdMap, String colHdCode) {

    }
}
