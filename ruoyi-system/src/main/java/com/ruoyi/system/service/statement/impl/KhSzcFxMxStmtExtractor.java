package com.ruoyi.system.service.statement.impl;

import com.ruoyi.common.enums.u8c.SubjDirection;
import com.ruoyi.common.u8c.AuxAcctProject;
import com.ruoyi.common.u8c.AuxBalance;
import com.ruoyi.common.u8c.warpper.AuxBalanceDataWrapper;
import com.ruoyi.common.u8c.warpper.FinancialDataWrapper;
import com.ruoyi.common.utils.ExcelUtils;
import com.ruoyi.system.domain.statement.CellWriter;
import com.ruoyi.system.domain.statement.RowColHeadIndex;
import com.ruoyi.system.domain.statement.StmtGenContext;
import com.ruoyi.system.domain.statement.cfg.StmtCfgWrapper;
import com.ruoyi.system.service.statement.BaseStmtExtractor;
import com.ruoyi.system.service.statement.ValueExtractor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 客户收支差分析明细表提取器
 */
@Component
public class KhSzcFxMxStmtExtractor extends BaseStmtExtractor implements ValueExtractor<Object> {
    @Override
    public List<CellWriter<Object>> extract(FinancialDataWrapper data, RowColHeadIndex rcHeadIndex,
                                            StmtCfgWrapper stmtCfg, Integer index, StmtGenContext context) {
        List<CellWriter<Object>> result = new ArrayList<>();
        Map<String, Map<String, BigDecimal>> subjBalanceMap = new HashMap<>();

        int costColi = colIndex(rcHeadIndex, "客户");
        int objColi = colIndex(rcHeadIndex, "项目名称");
        int dqMlColi = colIndex(rcHeadIndex, "当期毛利");
        int sqMlColi = colIndex(rcHeadIndex, "上期毛利");
        int srHbColi = colIndex(rcHeadIndex, "收入环比");
        int cbHbColi = colIndex(rcHeadIndex, "成本环比");
        int mlHbColi = colIndex(rcHeadIndex, "毛利环比");
        int col6001i = colIndex(rcHeadIndex, "6001");
        int col6401i = colIndex(rcHeadIndex, "6401");
        int colP6001i = colIndex(rcHeadIndex, "pre-6001");
        int colP6401i = colIndex(rcHeadIndex, "pre-6401");
        if (costColi < 0 || objColi < 0 || dqMlColi < 0 || sqMlColi < 0 || srHbColi < 0 || cbHbColi < 0 || mlHbColi < 0) {
            return result;
        }

        AuxBalanceDataWrapper auxBalanceDataWrapper = data.getAuxBalanceDataWrapper();
        byPeriod(auxBalanceDataWrapper.getCurrPeriod(), subjBalanceMap, true);
        byPeriod(auxBalanceDataWrapper.getPrePeriod(), subjBalanceMap, false);

        int rowI = 2;
        for (Map.Entry<String, Map<String, BigDecimal>> entry : subjBalanceMap.entrySet()) {
            String[] keyList = entry.getKey().split("_");
            String costName = keyList[0];   // 客户名称
            String objName = keyList[1];    // 项目名称

            addCellWriter(result, rowI, costColi, costName);
            addCellWriter(result, rowI, objColi, objName);

            int r = rowI;
            entry.getValue().forEach((k, v) -> {
                int coli = colIndex(rcHeadIndex, k);
                if (coli < 0) return;
                addCellWriter(result, r, coli, v);
            });

            int rowNum = rowI + 1;
            String letter1 = ExcelUtils.getExcelColumnLetter(col6001i);
            String letter2 = ExcelUtils.getExcelColumnLetter(col6401i);
            String formula1 = String.format("%s%d-%s%d", letter1, rowNum, letter2, rowNum);
            addCellWriter(result, rowI, dqMlColi, formula1, true);

            String letter3 = ExcelUtils.getExcelColumnLetter(colP6001i);
            String letter4 = ExcelUtils.getExcelColumnLetter(colP6401i);
            String formula2 = String.format("%s%d-%s%d", letter3, rowNum, letter4, rowNum);
            addCellWriter(result, rowI, sqMlColi, formula2, true);

            String formula3 = String.format("%s%d-%s%d", letter1, rowNum, letter3, rowNum);
            addCellWriter(result, rowI, srHbColi, formula3, true);

            String formula4 = String.format("%s%d-%s%d", letter2, rowNum, letter4, rowNum);
            addCellWriter(result, rowI, cbHbColi, formula4, true);

            String letter5 = ExcelUtils.getExcelColumnLetter(dqMlColi);
            String letter6 = ExcelUtils.getExcelColumnLetter(sqMlColi);
            String formula5 = String.format("%s%d-%s%d", letter5, rowNum, letter6, rowNum);
            addCellWriter(result, rowI, mlHbColi, formula5, true);

            rowI++;
        }
        return result;
    }

    private void byPeriod(List<AuxBalance> abList, Map<String, Map<String, BigDecimal>> subjBalanceMap, boolean isCurr) {
        abList.forEach(ab -> {
            List<AuxAcctProject> aapList = ab.getGlqueryassvo();
            if (CollectionUtils.isEmpty(aapList)) return;

            String key = aapList.stream().map(AuxAcctProject::getAssname).collect(Collectors.joining("_"));
            Map<String, BigDecimal> aRow = subjBalanceMap.computeIfAbsent(key, k -> new HashMap<>());
            String subjCode = ab.getPk_accsubj_code();
            String dqKey = isCurr ? subjCode : "pre-" + subjCode;
            String ljKey = (isCurr ? "" : "pre-") + "lj-" + subjCode;
            // 6001取贷，6401取借
            BigDecimal bq = subjCode.equals("6001")     // 本期
                    ? currBalance(ab, SubjDirection.CREDIT) : currBalance(ab, SubjDirection.DEBIT);
            BigDecimal lj = subjCode.equals("6001")     // 累计
                    ? accumBalance(ab, SubjDirection.CREDIT) : accumBalance(ab, SubjDirection.DEBIT);

            mergeBigDecimal(aRow, dqKey, bq);
            mergeBigDecimal(aRow, ljKey, lj);
        });
    }
}
