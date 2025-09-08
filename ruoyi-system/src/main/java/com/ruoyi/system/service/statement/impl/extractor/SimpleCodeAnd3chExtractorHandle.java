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

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 列头只有简单科目(每个头只有一个科目，不包含其他科目、“+”、“，”)
 * 行头是固定的3列：年初余额(期初数)、本期增加(本期数)、期末余额(期末数)
 */
public class SimpleCodeAnd3chExtractorHandle extends BaseStmtExtractor implements ExtractorHandle {
    private final Map<String, HeadKeyValHandle> HEAD_KEY_VAL_HANDLE = new HashMap<>();

    {
        HEAD_KEY_VAL_HANDLE.put("年初余额", this::init);
        HEAD_KEY_VAL_HANDLE.put("本期增加", this::curr);
        HEAD_KEY_VAL_HANDLE.put("期末余额", this::end);
    }

    @Override
    public void handle(List<CellWriter<Object>> result, FinancialDataWrapper data, RowColHeadIndex rcHeadIndex,
                       List<String> corpList, StmtGenContext context) {
        Map<String, Map<String, BigDecimal>> bCountMap = new HashMap<>();
        rcHeadIndex.getColHeadIdx().keySet().forEach(colKey -> bCountMap.computeIfAbsent(colKey, k -> new HashMap<>()));
        Set<String> subjSet = bCountMap.keySet();

        change(HEAD_KEY_VAL_HANDLE, context);

        SubjBalanceDataWrapper subjBalanceDataWrapper = data.getSubjBalanceDataWrapper();
        corpList.forEach(corp -> {
            List<SubjBalance> sbList = subjBalanceDataWrapper.getByCorpCode(corp);
            if (CollectionUtils.isEmpty(sbList)) return;

            sbList.forEach(sb -> {
                String code = sb.getPk_accsubj_code();
                String pCode = findPCode(code, subjSet);
                if (pCode == null) return;
                Map<String, BigDecimal> aRow = bCountMap.get(pCode);
                if (aRow == null) return;

                SubjDirection subjDirection = subjDirection(pCode);
                HEAD_KEY_VAL_HANDLE.forEach((rowKey, v) -> {
                    if (v == null) return;
                    mergeBigDecimal(aRow, rowKey, v.getVal(sb, code, pCode, subjDirection));
                });
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

    /**
     * 默认的期初取数逻辑
     * @param sb
     * @param code
     * @param direction
     * @return
     */
    private BigDecimal init(SubjBalance sb, String code, String pCode, SubjDirection direction) {
        return subtractInitBalance(sb, direction);
    }

    /**
     * 默认的本期取数逻辑
     * @param sb
     * @param code
     * @param direction
     * @return
     */
    private BigDecimal curr(SubjBalance sb, String code, String pCode, SubjDirection direction) {
        return currBalance(sb, direction);
    }

    /**
     * 默认的期末取数逻辑
     * @param sb
     * @param code
     * @param direction
     * @return
     */
    private BigDecimal end(SubjBalance sb, String code, String pCode, SubjDirection direction) {
        return subtractEndBalance(sb, direction);
    }

    /**
     * 改变或新增colKey的取值逻辑
     */
    protected void change(Map<String, HeadKeyValHandle> handleMap, StmtGenContext cxt) {

    }
}
