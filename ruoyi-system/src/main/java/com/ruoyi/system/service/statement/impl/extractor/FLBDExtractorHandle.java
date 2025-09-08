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
 * 分类变动类的sheet的通用提取逻辑
 */
public class FLBDExtractorHandle extends BaseStmtExtractor implements ExtractorHandle {
    // 基础的列头(分类变动类的表都有这些列头)其对应的取值逻辑映射关系
    private final Map<String, HeadKeyValHandle> BASE_COL_KEYS_HANDLE_MAPPER = new HashMap<>();

    {
        // 期初的默认实现
        BASE_COL_KEYS_HANDLE_MAPPER.put("一、年初数", this::init);
        // 本年提取的默认实现取本期数
        BASE_COL_KEYS_HANDLE_MAPPER.put("三、本年提取(摊销)", this::curr);
        // 期末的默认实现
        BASE_COL_KEYS_HANDLE_MAPPER.put("六、期末数", this::end);
    }

    @Override
    public void handle(List<CellWriter<Object>> result, FinancialDataWrapper data, RowColHeadIndex rcHeadIndex,
                       List<String> corpList, StmtGenContext context) {
        Set<String> subjSet = rcHeadIndex.getRowHeadIdx().keySet();
        Map<String, Map<String, BigDecimal>> bCountMap = new HashMap<>();

        change(BASE_COL_KEYS_HANDLE_MAPPER);

        SubjBalanceDataWrapper subjBalanceDataWrapper = data.getSubjBalanceDataWrapper();
        corpList.forEach(corp -> {
            List<SubjBalance> sbList = subjBalanceDataWrapper.getByCorpCode(corp);
            if (CollectionUtils.isEmpty(sbList)) return;

            subjSet.forEach(pSubj -> sbList.forEach(sb -> {
                String subj = sb.getPk_accsubj_code();
                if (!subj.startsWith(pSubj)) return;

                boolean b = excludeSubCode(subj, pSubj, subjSet);
                if (b) return;

                SubjDirection subjDirection = confirmDirection(subj, pSubj);
                BASE_COL_KEYS_HANDLE_MAPPER.forEach((rowKey, v) -> {
                    BigDecimal val = null;
                    if (v != null) {
                        val = v.getVal(sb, subj, pSubj, subjDirection);
                    }
                    mergeBigDecimal(bCountMap.computeIfAbsent(rowKey, k -> new HashMap<>()), pSubj, val);
                });
            }));

            bCountMap.forEach((rowKey, row) -> {
                int ri = rowIndex(rcHeadIndex, rowKey);
                row.forEach((colKey, val) -> {
                    int newRi = changeRowIdx(rowKey, colKey, ri);
                    int ci = colIndex(rcHeadIndex, colKey);
                    addCellWriter(result, newRi, ci, val);
                });
            });
        });
    }

    /**
     * 确认科目方向，默认是返回科目的原始方向
     * @param code
     * @param pCode
     * @return
     */
    protected SubjDirection confirmDirection(String code, String pCode) {
        return subjDirection(pCode);
    }

    /**
     * 改变或新增colKey的取值逻辑
     */
    protected void change(Map<String, HeadKeyValHandle> handleMap) {

    }

    /**
     * 是否排除某个科目，默认不排除
     * @param code
     * @param pCode
     * @param subjSet
     * @return
     */
    protected boolean excludeSubCode(String code, String pCode, Set<String> subjSet) {
        return false;
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

    protected int changeRowIdx(String rowKey, String colKey, int oldRi) {
        return oldRi;
    }
}
