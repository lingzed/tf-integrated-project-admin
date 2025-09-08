package com.ruoyi.system.service.statement.impl;

import com.ruoyi.common.enums.u8c.SubjDirection;
import com.ruoyi.common.u8c.AuxAcctProject;
import com.ruoyi.common.u8c.AuxBalance;
import com.ruoyi.common.u8c.SubjBalance;
import com.ruoyi.common.u8c.warpper.FinancialDataWrapper;
import com.ruoyi.system.domain.statement.CellWriter;
import com.ruoyi.system.domain.statement.RowColHeadIndex;
import com.ruoyi.system.domain.statement.StmtGenContext;
import com.ruoyi.system.domain.statement.cfg.SheetWriteCfg;
import com.ruoyi.system.domain.statement.cfg.StmtCfgWrapper;
import com.ruoyi.system.service.statement.BaseStmtExtractor;
import com.ruoyi.system.service.statement.ValueExtractor;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

/**
 * 利润分解表提取器
 */
@Component
public class LrFjStmtExtractor extends BaseStmtExtractor implements ValueExtractor<BigDecimal> {
    private static final Logger log = LoggerFactory.getLogger(LrFjStmtExtractor.class);

    @Override
    public List<CellWriter<BigDecimal>> extract(FinancialDataWrapper data, RowColHeadIndex rcHeadIndex,
                                                StmtCfgWrapper stmtCfg, Integer index, StmtGenContext context) {
        List<CellWriter<BigDecimal>> result = new ArrayList<>();

        List<AuxBalance> abList = data.getAuxBalanceDataWrapper().getCurrPeriod();
        List<SubjBalance> sbList = data.getSubjBalanceDataWrapper().getCurrPeriod();
        if (CollectionUtils.isEmpty(abList) || CollectionUtils.isEmpty(sbList)) return result;
        SheetWriteCfg writeCfg = stmtCfg.getListWriteCfg().get(index);
        String corpCode = writeCfg.getSheetKey();
        Integer statType = writeCfg.getStatType();

        for (AuxBalance aa : abList) {
            String subjectId = aa.getPk_accsubj_code();  // 科目id
            List<AuxAcctProject> aapList = aa.getGlqueryassvo();  // 辅助核算项目集合
            if (CollectionUtils.isEmpty(aapList)) continue;
            String deptId = aapList.get(0).getAsscode();   // 辅助核算项目集合中只有部门这一个辅助

            // 科目对应的行索引
            int ri = rowIndex(rcHeadIndex, subjectId);
            // 部门对应的列索引
            int ci = colIndex(rcHeadIndex, deptId);
            if (ri < 0 || ci < 0) continue;

            // 得到方向
            SubjDirection direction = getDirection(writeCfg.getSheetKey(), subjectId);
            BigDecimal bys = currBalance(aa, direction);    // 本月数
            BigDecimal ljs = accumBalance(aa, direction);   // 累计数

            // 根据统计类型得到本月数或累计数，将其封装到CellWrite中，然后装入集合
            if (statType.equals(0)) {   // 本月数
                addCellWriter(result, ri, ci, bys);
            } else if (statType.equals(1)) {// 本年累计
                addCellWriter(result, ri, ci, ljs);
            } else if (statType.equals(2)) {
                addCellWriter(result, ri, ci, bys);
                addCellWriter(result, ri, ci + 1, ljs);
            } else {
                log.warn("没有这个统计类型：{}", statType);
            }
        }
        processKmBalance(corpCode, sbList, rcHeadIndex, statType, result);
        return result;
    }

    // 处理科目余额
    private void processKmBalance(String corp, List<SubjBalance> sbList, RowColHeadIndex rcHeadIndex, Integer statType, List<CellWriter<BigDecimal>> result) {
        Map<String, BigDecimal> subjTotal = new HashMap<>();
        rcHeadIndex.getColHeadIdx().keySet().forEach(subjCode ->
                sbList.forEach(sb -> {
                    String sCode = sb.getPk_accsubj_code();
                    if (!sCode.startsWith(subjCode)) return;

                    SubjDirection direction = getDirection(corp, sCode);
                    if (statType.equals(0)) {
                        BigDecimal bys = currBalance(sb, direction);  // 本月数
                        mergeBigDecimal(subjTotal, subjCode, bys);
                    } else if (statType.equals(1)) {
                        BigDecimal ljs = accumBalance(sb, direction);  // 本年累计
                        mergeBigDecimal(subjTotal, subjCode, ljs);

                    } else if (statType.equals(2)) {
                        BigDecimal bys = currBalance(sb, direction);  // 本月数
                        BigDecimal ljs = accumBalance(sb, direction);  // 本年累计
                        mergeBigDecimal(subjTotal, subjCode + "m", bys);
                        mergeBigDecimal(subjTotal, subjCode + "y", ljs);

                    } else {
                        log.warn("没有这个统计类型：{}", statType);
                    }
                }));

        subjTotal.forEach((subjCode, value) -> {
            int ci = colIndex(rcHeadIndex, "科目余额");
            if (statType.equals(0) || statType.equals(1)) {
                int ri = rowIndex(rcHeadIndex, subjCode);
                BigDecimal val = subjTotal.get(subjCode);
                addCellWriter(result, ri, ci, val);

            } else if (statType.equals(2)) {
                String nSubjCode = subjCode.substring(0, subjCode.length() - 1);
                int ri = rowIndex(rcHeadIndex, nSubjCode);
                if (subjCode.charAt(subjCode.length() - 1) == 'm') {
                    addCellWriter(result, ri, ci, value);
                } else {
                    addCellWriter(result, ri, ci + 1, value);
                }
            }
        });
    }

    private SubjDirection getDirection(String corp, String subjCode) {
        List<String> condition = Arrays.asList("A05", "A01", "A02", "A03");
        if (subjCode.equals("221102") && condition.contains(corp.substring(0, 3))) {
            return SubjDirection.DEBIT;
        } else {
            return subjDirection(subjCode);
        }
    }
}
