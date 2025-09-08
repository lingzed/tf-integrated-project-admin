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
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

/**
 * 财务费用sheet提取逻辑
 */
@Component("CWFY")
public class CWFYExtractorHandle extends BaseStmtExtractor implements ExtractorHandle {
    private static final String BNLJ = "本年累计";
    private static final Logger log = LoggerFactory.getLogger(CWFYExtractorHandle.class);

    @Override
    public void handle(List<CellWriter<Object>> result, FinancialDataWrapper data, RowColHeadIndex rcHeadIndex,
                       List<String> corpList, StmtGenContext context) {
        SubjBalanceDataWrapper subjBalanceDataWrapper = data.getSubjBalanceDataWrapper();
        Map<String, Map<String, BigDecimal>> bCountMap = new HashMap<>();

        // 先把列头的科目放到bCountMap中，科目为父级
        rcHeadIndex.getColHeadIdx().keySet().forEach(subj -> {
            if (subj.contains("+")) {
                String[] split = subj.split("\\+");
                for (String s : split) {
                    bCountMap.computeIfAbsent(s, k -> new HashMap<>());
                }
            } else {
                bCountMap.computeIfAbsent(subj, k -> new HashMap<>());
            }
        });

        // 遍历公司进行累计
        corpList.forEach(corp -> {
            List<SubjBalance> sbList = subjBalanceDataWrapper.getByCorpCode(corp);
            if (CollectionUtils.isEmpty(sbList)) return;
            // 遍历bCountMap，其父级科目对应的子级进行累计
            bCountMap.forEach((subj, aRow) -> sbList.forEach(sb -> {
                String subSubj = sb.getPk_accsubj_code();
                if (!subSubj.startsWith(subj)) return;

                BigDecimal ljs = accumBalance(sb, subjDirection(subSubj)); // 累计数
                ljs = subj.equals("660301") ? ljs.negate() : ljs;
                mergeBigDecimal(aRow, BNLJ, ljs);
            }));
        });
        // 自合并
        selfBdMerge(bCountMap, "+", true, "660303", "660304", "660398");

        bCountMap.forEach((subj, aRow) -> {
            int ri = rowIndex(rcHeadIndex, subj);
            aRow.forEach((k, v) -> {
                int ci = colIndex(rcHeadIndex, k);
                addCellWriter(result, ri, ci, v == null ? BigDecimal.TEN : v);
            });
        });
    }

}
