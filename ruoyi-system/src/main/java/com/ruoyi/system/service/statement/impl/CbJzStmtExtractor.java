package com.ruoyi.system.service.statement.impl;

import com.ruoyi.common.enums.u8c.SubjDirection;
import com.ruoyi.common.u8c.AuxAcctProject;
import com.ruoyi.common.u8c.AuxBalance;
import com.ruoyi.system.domain.statement.CellWriter;
import com.ruoyi.system.domain.statement.StmtGenContext;
import com.ruoyi.common.u8c.warpper.AuxBalanceDataWrapper;
import com.ruoyi.common.u8c.warpper.FinancialDataWrapper;
import com.ruoyi.common.utils.ExcelUtils;
import com.ruoyi.system.domain.statement.RowColHeadIndex;
import com.ruoyi.system.domain.statement.cfg.StmtCfgWrapper;
import com.ruoyi.system.service.statement.BaseStmtExtractor;
import com.ruoyi.system.service.statement.ValueExtractor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 成本结转表提取器
 */
@Component
public class CbJzStmtExtractor extends BaseStmtExtractor implements ValueExtractor<Object> {
    private static final String QCYE = "期初余额";
    private static final String FSCB = "发生成本";
    private static final String JZCB = "结转成本";
    private static final String JYCB = "结余成本";
    private static final String HJ = "合计";

    @Override
    public List<CellWriter<Object>> extract(FinancialDataWrapper data, RowColHeadIndex rcHeadIndex,
                                            StmtCfgWrapper stmtCfg, Integer index, StmtGenContext context) {
        List<CellWriter<Object>> result = new ArrayList<>();
        Map<String, Map<String, BigDecimal>> rowBalanceMap1 = new HashMap<>();
        Map<String, Map<String, BigDecimal>> rowBalanceMap2 = new HashMap<>();

        int deptColi = colIndex(rcHeadIndex, "部门");   // 部门列索引
        int objColi = colIndex(rcHeadIndex, "项目名称");  // 项目列索引
        int costColi = colIndex(rcHeadIndex, "客商");   // 客商列索引
        int btColi = colIndex(rcHeadIndex, "balanceType");   // 余额类型列索引
        if (deptColi < 0 || objColi < 0 || btColi < 0 || costColi < 0) return result;

        List<AuxBalance> abList = reviewData(data);
        abList.forEach(ab -> {
            List<AuxAcctProject> aapList = ab.getGlqueryassvo();
            if (CollectionUtils.isEmpty(aapList)) return;

            List<String> collect = aapList.stream().map(AuxAcctProject::getAssname).collect(Collectors.toList());
            String key = String.join("_", collect);

            Map<String, BigDecimal> aRow1 = rowBalanceMap1.computeIfAbsent(key, k -> new HashMap<>());
            Map<String, BigDecimal> aRow2 = rowBalanceMap2.computeIfAbsent(key, k -> new HashMap<>());

            String subjCode = ab.getPk_accsubj_code();  // 科目编码
            BigDecimal initDebitLocAmount = ab.getInitDebitLocAmount();     // 期初借方
            BigDecimal initCreditLocAmount = ab.getInitCreditLocAmount();   // 期初贷方
            BigDecimal initBalance = bdSubtract(initDebitLocAmount, initCreditLocAmount);   // 期初数
            BigDecimal debitLocAmount = currBalance(ab, SubjDirection.DEBIT); // 本期借

            mergeBigDecimal(aRow1, subjCode, initBalance);
            mergeBigDecimal(aRow2, subjCode, debitLocAmount);
        });

        int rowI = 3;
        int startRowNum = rowI + 1;     // 开始写入的行号
        for (Map.Entry<String, Map<String, BigDecimal>> entry : rowBalanceMap1.entrySet()) {
            String key = entry.getKey();
            String[] split = key.split("_");
            String deptName = split[0];
            String objName = split[1];
            String costName = split[2];

            addCellWriter(result, rowI, deptColi, deptName);
            addCellWriter(result, rowI, objColi, objName);
            addCellWriter(result, rowI, costColi, costName);
            addCellWriter(result, rowI, btColi, QCYE);

            int i = rowI;
            Map<String, BigDecimal> subjBalanceMap1 = entry.getValue();
            subjBalanceMap1.forEach((subjCode, balance) -> {
                int colIndex = colIndex(rcHeadIndex, subjCode);
                if (colIndex < 0) return;
                addCellWriter(result, i, colIndex, balance);
            });

            int next = rowI + 1;
            addCellWriter(result, next, deptColi, deptName);
            addCellWriter(result, next, objColi, objName);
            addCellWriter(result, next, costColi, costName);
            addCellWriter(result, next, btColi, FSCB);

            Map<String, BigDecimal> subjBalanceMap2 = rowBalanceMap2.get(key);
            subjBalanceMap2.forEach((subjCode, balance) -> {
                int colIndex = colIndex(rcHeadIndex, subjCode);
                if (colIndex < 0) return;
                addCellWriter(result, next, colIndex, balance);
            });

            int aNext = rowI + 2;
            addCellWriter(result, aNext, deptColi, deptName);
            addCellWriter(result, aNext, objColi, objName);
            addCellWriter(result, aNext, costColi, costName);
            addCellWriter(result, aNext, btColi, JZCB);

            int last = rowI + 3;
            addCellWriter(result, last, deptColi, deptName);
            addCellWriter(result, last, objColi, objName);
            addCellWriter(result, last, costColi, costName);
            addCellWriter(result, last, btColi, JYCB);
            // 不能单用subjBalanceMap1或subjBalanceMap2的科目来确定公式的列，因为如果单用某一个，那么其中一个没有的科目可能在另一个里面就有
            // 所以得合并
            Set<String> useful = Stream.concat(subjBalanceMap1.keySet().stream(), subjBalanceMap2.keySet().stream())
                    .collect(Collectors.toSet());
            useful.forEach(subjCode -> {
                int colIndex = colIndex(rcHeadIndex, subjCode);
                if (colIndex < 0) return;
                String colLetter = ExcelUtils.getExcelColumnLetter(colIndex);   // 索引得到字母
                String formula = colLetter + next + "+" + colLetter + aNext + "-" + colLetter + last;
                addCellWriter(result, last, colIndex, formula, true);
            });

            rowI += 4;
        }

        int lastRow1 = rowI;
        int lastRow2 = rowI + 1;
        int lastRow3 = rowI + 2;
        int lastRow4 = rowI + 3;

        addCellWriter(result, lastRow1, deptColi, HJ);
        addCellWriter(result, lastRow1, btColi, QCYE);
        addCellWriter(result, lastRow2, deptColi, HJ);
        addCellWriter(result, lastRow2, btColi, FSCB);
        addCellWriter(result, lastRow3, deptColi, HJ);
        addCellWriter(result, lastRow3, btColi, JZCB);
        addCellWriter(result, lastRow4, deptColi, HJ);
        addCellWriter(result, lastRow4, btColi, JYCB);

        Set<String> subjSet = rcHeadIndex.getRowHeadIdx().keySet();
        List<String> remove = Arrays.asList("部门", "项目名称", "客商", "balanceType");
        subjSet.stream().filter(e -> !remove.contains(e))
                .forEach(subj -> {
                    int colIndex = colIndex(rcHeadIndex, subj);
                    if (colIndex < 0) return;
                    String colLetter = ExcelUtils.getExcelColumnLetter(colIndex);
                    String formula1 = String.format("SUMIF(D%d:D%d, \"%s\", %s%d:%s%d)", startRowNum, lastRow1, QCYE, colLetter, startRowNum, colLetter, lastRow1);
                    String formula2 = String.format("SUMIF(D%d:D%d, \"%s\", %s%d:%s%d)", startRowNum, lastRow1, FSCB, colLetter, startRowNum, colLetter, lastRow1);
                    String formula3 = String.format("SUMIF(D%d:D%d, \"%s\", %s%d:%s%d)", startRowNum, lastRow1, JZCB, colLetter, startRowNum, colLetter, lastRow1);
                    String formula4 = String.format("SUMIF(D%d:D%d, \"%s\", %s%d:%s%d)", startRowNum, lastRow1, JYCB, colLetter, startRowNum, colLetter, lastRow1);
                    addCellWriter(result, lastRow1, colIndex, formula1, true);
                    addCellWriter(result, lastRow2, colIndex, formula2, true);
                    addCellWriter(result, lastRow3, colIndex, formula3, true);
                    addCellWriter(result, lastRow4, colIndex, formula4, true);
                });

        return result;
    }

    private List<AuxBalance> reviewData(FinancialDataWrapper data) {
        return Optional.ofNullable(data)
                .map(FinancialDataWrapper::getAuxBalanceDataWrapper)
                .map(AuxBalanceDataWrapper::getCurrPeriod)
                .orElse(Collections.emptyList());
    }


}
