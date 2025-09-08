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
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

/**
 * 管理费用sheet提取逻辑
 */
@Component("GLFY")
public class GLFYExtractorHandle extends BaseStmtExtractor implements ExtractorHandle {
    private static final String colKey = "本年累计";
    private static final String rowKey = "其他";
    private static final Logger log = LoggerFactory.getLogger(GLFYExtractorHandle.class);

    @Override
    public void handle(List<CellWriter<Object>> result, FinancialDataWrapper data, RowColHeadIndex rcHeadIndex,
                       List<String> corpList, StmtGenContext context) {
        SubjBalanceDataWrapper subjBalanceDataWrapper = data.getSubjBalanceDataWrapper();
        Map<String, Map<String, BigDecimal>> bCountMap = new HashMap<>();

        Set<String> subjSet = new HashSet<>();
        Set<String> exclude = new HashSet<>();  // 待排除的编码
        rcHeadIndex.getColHeadIdx().keySet().forEach(subj -> {
            if (subj.contains("+")) {
                exclude.add(subj);  // 需要把带+的编码增加到待排除的列表中，因为之后排除的编码中带有+的编码也要被排除
                subjSet.addAll(Arrays.asList(subj.split("\\+")));
            } else {
                subjSet.add(subj);
            }
        });
        exclude.addAll(subjSet);

        corpList.forEach(corp -> {
            List<SubjBalance> sbList = subjBalanceDataWrapper.getByCorpCode(corp);
            if (CollectionUtils.isEmpty(sbList)) return;

            sbList.forEach(sb -> {
                String subjCode = sb.getPk_accsubj_code();
                // 记录需要排除的编码
                if (subjSet.stream().anyMatch(subjCode::startsWith)) {
                    exclude.add(subjCode);
                }
                SubjDirection subjDirection = subjDirection(subjCode);
                BigDecimal bq = currBalance(sb, subjDirection); //本期
                // 直接用abList中的编码作为key，这里有些编码是子编码
                Map<String, BigDecimal> aRow = bCountMap.computeIfAbsent(subjCode, k -> new HashMap<>());
                mergeBigDecimal(aRow, colKey, bq);
            });
        });

        Map<String, Map<String, BigDecimal>> temp = new HashMap<>();
        subjSet.forEach(pSubj -> bCountMap.forEach((subj, aRow) -> {
            if (!subj.startsWith(pSubj)) return;
            if (subj.equals(pSubj)) return;
            Map<String, BigDecimal> tempRow = temp.computeIfAbsent(pSubj, k -> new HashMap<>());
            aRow.forEach((k, v) -> mergeBigDecimal(tempRow, k, v));
        }));
        bCountMap.putAll(temp);

        // 自和并
        selfBdMerge(bCountMap, "+", true, "66020301", "66020303");
        selfBdMerge(bCountMap, "+", true, "66020204", "66020205", "66020208", "66020299");
        selfBdMerge(bCountMap, "+", true, "660205", "66022501", "66022502", "66022503", "66022504");
        selfBdMergeExcludeKey(bCountMap, rowKey, true, exclude.toArray(new String[]{}));
//        log.info("bCountMap: {}", JSON.toJSONString(bCountMap));
//        log.info("exclude: {}", exclude);
        bCountMap.forEach((rowKey, row) -> {
            int ri = rowIndex(rcHeadIndex, rowKey);
            row.forEach((colKey, val) -> {
                int ci = colIndex(rcHeadIndex, colKey);
                addCellWriter(result, ri, ci, val);
            });
        });
    }
}
