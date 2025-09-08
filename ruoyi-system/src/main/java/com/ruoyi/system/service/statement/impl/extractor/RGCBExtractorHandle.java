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
 * 人工成本sheet提取逻辑
 */
@Component("RGCB")
public class RGCBExtractorHandle extends BaseStmtExtractor implements ExtractorHandle {
    private static final String[] rowKeys = new String[]{
            "合同制员工", "劳务派遣人员"
    };
    private static final Logger log = LoggerFactory.getLogger(RGCBExtractorHandle.class);

    @Override
    public void handle(List<CellWriter<Object>> result, FinancialDataWrapper data, RowColHeadIndex rcHeadIndex,
                       List<String> corpList, StmtGenContext context) {
        Map<String, Map<String, BigDecimal>> bCountMap = new HashMap<>();
        Set<String> subjSet = new HashSet<>();
        Map<String, String> colHdMapper = new HashMap<>();  // 列头再次映射，以，分割然后映射原来的key
        Map<String, Set<String>> needCount = new HashMap<>();
        List<String> remove = Arrays.asList("221105", "22110805", "2211");

        rcHeadIndex.getColHeadIdx().keySet().forEach(key -> {
            if (key.contains(", ")) {
                String[] keySplit = key.split(", ");
                for (int i = 0; i < keySplit.length; i++) {
                    String subKey = keySplit[i];
                    if (subKey.contains("+")) {
                        String[] subKeySplit = subKey.split("\\+");
                        subjSet.addAll(Arrays.asList(subKeySplit));
                    } else {
                        subjSet.add(subKey);
                    }
                    colHdMapper.put(subKey, key);
                }
            } else {
                colHdMapper.put(key, key);
                subjSet.add(key);
            }
        });
        remove.forEach(subjSet::remove);

        SubjBalanceDataWrapper subjBalanceDataWrapper = data.getSubjBalanceDataWrapper();
        corpList.forEach(corp -> {
            List<SubjBalance> sbList = subjBalanceDataWrapper.getByCorpCode(corp);
            if (CollectionUtils.isEmpty(sbList)) return;

            sbList.forEach(sb -> {
                String code = sb.getPk_accsubj_code();
                if (!subjSet.contains(code) && subjSet.stream().noneMatch(code::startsWith)) {
                    recordNeedCount(needCount, code, remove);
                }

                BigDecimal bq = currBalance(sb, subjDirection(code)); // 本期
                Map<String, BigDecimal> aRow = bCountMap.computeIfAbsent(code, k -> new HashMap<>());
                mergeBigDecimal(aRow, code.contains("221108") ? rowKeys[1] : rowKeys[0], bq);
            });
        });

        Map<String, Map<String, BigDecimal>> temp = new HashMap<>();
        subjSet.forEach(pSubj -> bCountMap.forEach((subj, aRow) -> {
            if (subj.equals(pSubj)) return;
            if (!subj.startsWith(pSubj)) return;

            Map<String, BigDecimal> tempRow = temp.computeIfAbsent(pSubj, k -> new HashMap<>());
            aRow.forEach((k, v) -> mergeBigDecimal(tempRow, k, v));
        }));
        bCountMap.putAll(temp);

        // 22110503+22110507
        selfBdMerge(bCountMap, "+", true, "22110503", "22110507");
        // 父级合计(排除bCountMap有的编码)
        needCount.forEach((code, countSet) -> selfBdMerge(bCountMap, code, null, true, countSet.toArray(new String[]{})));

        bCountMap.forEach((rowKey, row) -> {
            int ri = rowIndex(rcHeadIndex, colHdMapper.get(rowKey));
            row.forEach((colKey, val) -> {
                int ci = colIndex(rcHeadIndex, colKey);
                addCellWriter(result, ri, ci, val);
            });
        });
    }

    private void recordNeedCount(Map<String, Set<String>> needCount, String code, List<String> codes) {
        for (String c : codes) {
            if (!code.startsWith(c)) continue;
            needCount.computeIfAbsent(c, k -> new HashSet<>()).add(code);
            return;
        }
    }
}
