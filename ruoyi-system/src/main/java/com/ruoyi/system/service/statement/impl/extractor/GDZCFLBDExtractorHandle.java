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
import java.util.*;

/**
 * 固定资产分类变动sheet提取逻辑
 */
//@Component("GDZCFLBD")
public class GDZCFLBDExtractorHandle extends BaseStmtExtractor implements ExtractorHandle {
    private static final String[] rowKeys = new String[]{
            "一、年初数",
            "零星购置",
            "三、本年提取(摊销)",
            "六、期末数"
    };

    @Override
    public void handle(List<CellWriter<Object>> result, FinancialDataWrapper data, RowColHeadIndex rcHeadIndex,
                       List<String> corpList, StmtGenContext context) {
        SubjBalanceDataWrapper subjBalanceDataWrapper = data.getSubjBalanceDataWrapper();
        Set<String> subjSet = rcHeadIndex.getRowHeadIdx().keySet();
        Map<String, Map<String, BigDecimal>> bCountMap = new HashMap<>();

        corpList.forEach(corp -> {
            List<SubjBalance> sbList = subjBalanceDataWrapper.getByCorpCode(corp);
            if (CollectionUtils.isEmpty(sbList)) return;

            subjSet.forEach(pSubj -> sbList.forEach(sb -> {
                String subj = sb.getPk_accsubj_code();
                if (!subj.startsWith(pSubj)) return;

                // subj是pSubj的子编码且在subjSet中出现过就排除
                // 这个排除是因为subjSet中的编码存在有父子编码的情况，客户的意思是子编码已经单独出去了，那么父编码里面也应该减去
                if (!subj.equals(pSubj) && subjSet.contains(subj)) return;

                SubjDirection subjDirection = subjDirection(pSubj);
                BigDecimal init = subtractInitBalance(sb, subjDirection);   // 期初
                boolean is1601 = pSubj.equals("1601");
                // 1601取借方的数，其它为null
                BigDecimal bqj = is1601 ? currBalance(sb, SubjDirection.DEBIT) : null;  // 本期借
                // 非1601取贷方的数，1601为null
                BigDecimal bqd = is1601 ? null : currBalance(sb, SubjDirection.CREDIT);  // 本期贷
                BigDecimal end = subtractEndBalance(sb, subjDirection); // 期末数
                BigDecimal[] bArr = new BigDecimal[]{init, bqj, bqd, end};

                for (int i = 0; i < rowKeys.length; i++) {
                    Map<String, BigDecimal> aRow = bCountMap.computeIfAbsent(rowKeys[i], k -> new HashMap<>());
                    mergeBigDecimal(aRow, pSubj, bArr[i]);
                }
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
