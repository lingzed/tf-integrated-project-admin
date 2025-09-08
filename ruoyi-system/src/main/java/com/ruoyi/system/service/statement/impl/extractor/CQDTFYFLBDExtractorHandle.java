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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 长期待摊费用分类变动sheet提取逻辑
 */
//@Component("CQDTFYFLBD")
public class CQDTFYFLBDExtractorHandle extends BaseStmtExtractor implements ExtractorHandle {
    private static final String[] rowKeys = new String[]{
            "一、年初数",
            "三、本年提取(摊销)",
            "六、期末数"
    };
    private static final Logger log = LoggerFactory.getLogger(CQDTFYFLBDExtractorHandle.class);

    @Override
    public void handle(List<CellWriter<Object>> result, FinancialDataWrapper data, RowColHeadIndex rcHeadIndex,
                       List<String> corpList, StmtGenContext context) {
        SubjBalanceDataWrapper subjBalanceDataWrapper = data.getSubjBalanceDataWrapper();
        Map<String, Map<String, BigDecimal>> bCountMap = new HashMap<>();

        Set<String> pSubjSet = rcHeadIndex.getRowHeadIdx().keySet();    // 父级科目编码
        corpList.forEach(corp -> {
            List<SubjBalance> sbList = subjBalanceDataWrapper.getByCorpCode(corp);
            if (CollectionUtils.isEmpty(sbList)) return;

            pSubjSet.forEach(pSubj -> sbList.forEach(sb -> {
                String subj = sb.getPk_accsubj_code();
                if (!subj.startsWith(pSubj)) return;
                // 180101借，180102贷
                SubjDirection subjDirection = pSubj.equals("180101") ? SubjDirection.DEBIT : SubjDirection.CREDIT;
                BigDecimal init = subtractInitBalance(sb, subjDirection); // 期初
                BigDecimal bq = currBalance(sb, subjDirection); // 本期
                BigDecimal end = subtractEndBalance(sb, subjDirection);    // 期末
//                log.info("init:{},bq:{},end:{}", init, bq, end);
                BigDecimal[] bList = new BigDecimal[]{init, bq, end};

                for (int i = 0; i < rowKeys.length; i++) {
                    Map<String, BigDecimal> aRow = bCountMap.computeIfAbsent(rowKeys[i], k -> new HashMap<>());
                    mergeBigDecimal(aRow, pSubj, bList[i]);
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
