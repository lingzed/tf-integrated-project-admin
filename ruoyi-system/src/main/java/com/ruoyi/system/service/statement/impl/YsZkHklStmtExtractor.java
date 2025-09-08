package com.ruoyi.system.service.statement.impl;

import com.ruoyi.common.enums.u8c.SubjDirection;
import com.ruoyi.common.u8c.AuxAcctProject;
import com.ruoyi.common.u8c.AuxBalance;
import com.ruoyi.common.u8c.warpper.AuxBalanceDataWrapper;
import com.ruoyi.common.u8c.warpper.FinancialDataWrapper;
import com.ruoyi.common.utils.DateFormatUtil;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.statement.CellWriter;
import com.ruoyi.system.domain.statement.RowColHeadIndex;
import com.ruoyi.system.domain.statement.StmtGenContext;
import com.ruoyi.system.domain.statement.cfg.StmtCfgWrapper;
import com.ruoyi.system.service.statement.BaseStmtExtractor;
import com.ruoyi.system.service.statement.ValueExtractor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 应收账款回款率提取器
 */
@Component
public class YsZkHklStmtExtractor extends BaseStmtExtractor implements ValueExtractor<Object> {
    private static final Logger log = LoggerFactory.getLogger(YsZkHklStmtExtractor.class);
    private static final String LY_QC = "ly期初";
    private static final String LY_LJJ = "ly累计借";
    private static final String LY_LJD = "ly累计贷";
    private static final String LY_HKL = "ly回款率";
    private static final String Y_QC = "y期初";
    private static final String Y_LJJ = "y累计借";
    private static final String Y_LJD = "y累计贷";
    private static final String Y_HKL = "y回款率";
    private static final String Y2020_2 = "2020_2";
    private static final String INIT_2025 = "2025init";

    @Override
    public List<CellWriter<Object>> extract(FinancialDataWrapper data, RowColHeadIndex rcHeadIndex,
                                            StmtCfgWrapper stmtCfg, Integer index, StmtGenContext context) {
        List<CellWriter<Object>> result = new ArrayList<>();

        int lyInitColi = colIndex(rcHeadIndex, LY_QC);  // 去年期初数列索引
        int lyAccumJColi = colIndex(rcHeadIndex, LY_LJJ);     // 去年累计借列索引
        int lyAccumDColi = colIndex(rcHeadIndex, LY_LJD);    // 去年累计贷列索引
        int lyHklColi = colIndex(rcHeadIndex, LY_HKL);    // 去年回款率列索引
        int yInitColi = colIndex(rcHeadIndex, Y_QC);      // 期初列索引
        int yAccumJColi = colIndex(rcHeadIndex, Y_LJJ);     // 累计借列索引
        int yAccumDColi = colIndex(rcHeadIndex, Y_LJD);     // 累计贷列索引
        int yHklColi = colIndex(rcHeadIndex, Y_HKL);     // 回款率列索引
        int y2020Coli = colIndex(rcHeadIndex, Y2020_2);     // 2020年初始额2列索引
        if (Stream.of(lyInitColi, lyAccumJColi, lyAccumDColi, lyHklColi, yInitColi, yAccumJColi, yAccumDColi, yHklColi, y2020Coli)
                .anyMatch(i -> i < 0)) return result;

        int dCodeColi = colIndex(rcHeadIndex, "部门编码");     // 部门编码列索引
        int dNameColi = colIndex(rcHeadIndex, "部门名称");     // 部门名称列索引
        int subjColi = colIndex(rcHeadIndex, "科目");     // 科目列索引

        if (dCodeColi < 0 || dNameColi < 0 || subjColi < 0) return result;

        String period = context.periodStr();
        String year = String.valueOf(context.periodYear());
        YearMonth yearMonth = context.period();
        YearMonth lastYearMonth = yearMonth.minusYears(1);
        String lastYear = lastYearMonth.getYear() + "年";
        String yearRageDate = DateFormatUtil.formatRangeFromStart(period);
        String lastYearRageDate = DateFormatUtil.formatRangeFromStart(lastYearMonth.toString());

        String init = "期初数";
        String accumJ = "应收累计借方";
        String accumD = "应收累计贷方";
        String hkl = "回款率";
        addCellWriter(result, 0, lyInitColi, lastYear + init);
        addCellWriter(result, 0, lyAccumJColi, lastYearRageDate + accumJ);
        addCellWriter(result, 0, lyAccumDColi, lastYearRageDate + accumD);
        addCellWriter(result, 0, yInitColi, year + init);
        addCellWriter(result, 0, yAccumJColi, yearRageDate + accumJ);
        addCellWriter(result, 0, yAccumDColi, yearRageDate + accumD);
        addCellWriter(result, 0, lyHklColi, lastYearRageDate + hkl);
        addCellWriter(result, 0, yHklColi, yearRageDate + hkl);

        AuxBalanceDataWrapper auxBalanceDataWrapper = data.getAuxBalanceDataWrapper();
        int currYear = context.periodYear();    // 当前年
        // 本年1月至当月
        List<AuxBalance> firstPeriodToCurr = auxBalanceDataWrapper.getFirstPeriodToCurr();
        // 上年1-12月
        List<AuxBalance> lyList1to12 = auxBalanceDataWrapper.getPreYear();
        processByPeriodRange(firstPeriodToCurr, result, lyList1to12, context, currYear, dCodeColi, dNameColi, subjColi, yInitColi, yAccumJColi, yAccumDColi, y2020Coli);
        if (currYear > 2025) {
            // 上年1月至当月
            List<AuxBalance> preYearFirstToCurr = auxBalanceDataWrapper.getPreYearFirstToCurr();
            // 上上年1-12月
            List<AuxBalance> llyList1to12 = auxBalanceDataWrapper.getPPreYear();
            // 处理当年的上年，注意这里的当前年度已经是当年的上年，所以需要当年-1
            processByPeriodRange(preYearFirstToCurr, result, llyList1to12, context, currYear - 1, dCodeColi, dNameColi, subjColi, lyInitColi, lyAccumJColi, lyAccumDColi, y2020Coli);
        }

        return result;
    }

    private void processByPeriodRange(List<AuxBalance> list, List<CellWriter<Object>> result, List<AuxBalance> lyList,
                                      StmtGenContext context, int currYear, int dCodeColi, int dNameColi, int subjColi, int initColi, int ljjColI, int ljdColI, int y2020Coli) {
        if (CollectionUtils.isEmpty(list)) return;
        Map<String, List<AuxBalance>> bySubjCode = list.stream()
                .collect(Collectors.groupingBy(AuxBalance::getPk_accsubj_code));

        // 拿到上年1-12月的部门-最终期初数的映射，上年数据列表为空，则映射为空
        Map<String, BigDecimal> initBalanceMap = initBalanceMap(lyList);
        Set<String> deptKeySet = getDeptKeySet(context);  // 部门编码列表

        // 1122,2203的科目顺序，先统计每一行的数据
        Map<String, Map<Integer, BigDecimal>> aRowBalanceMap = new LinkedHashMap<>();
        boolean isDone1122 = false;   // 是否补充完1122
        for (String subjCode : Arrays.asList("1122", "2203")) {
            List<AuxBalance> abList = bySubjCode.get(subjCode);
            if (CollectionUtils.isEmpty(abList)) continue;

            // 补充相差的部门，只补充1122科目，且在1122遍历完成后立马补充
            if (subjCode.equals("2203") && !isDone1122 && CollectionUtils.isNotEmpty(deptKeySet)) {
                deptKeySet.forEach(deptKey -> {
                    Map<Integer, BigDecimal> aRow = aRowBalanceMap.computeIfAbsent(deptKey + "_应收账款_1122", k -> new HashMap<>());
                    BigDecimal y2020 = special(context, deptKey, Y2020_2);  // 2020年度值
                    mergeBigDecimal(aRow, y2020Coli, y2020);
                    if (currYear == 2025) {
                        BigDecimal init2025 = special(context, deptKey, INIT_2025);  // 2025年度期初值
                        mergeBigDecimal(aRow, initColi, init2025);
                    }
                });
                isDone1122 = true;
            }

            for (AuxBalance ab : abList) {
                List<AuxAcctProject> aapList = ab.getGlqueryassvo();
                if (CollectionUtils.isEmpty(aapList)) continue;

                String deptCode = aapList.get(0).getAsscode(); // 部门id
                String deptName = aapList.get(0).getAssname(); // 部门名称
                String deptKey = deptCode + "_" + deptName; // 部门Key
                deptKeySet.remove(deptKey);   // 移除部门key
                String subjName = ab.getPk_accsubj_name();  // 科目名称
                String key = deptKey + "_" + subjName + "_" + subjCode;
                Map<Integer, BigDecimal> aRow = aRowBalanceMap.computeIfAbsent(key, k -> new HashMap<>());

                SubjDirection subjDirection = subjDirection(subjCode);
                if (subjDirection == null) continue;
//                BigDecimal initBalance = initBalance(ab, subjDirection); // 期初数
                BigDecimal initBalance = currYear == 2025   // 如果是2025年，则从数据模板中取值，否则从最终期初数映射中取值
                        ? special(context, deptKey, INIT_2025)
                        : initBalanceMap.get(deptCode);   // 最终期初数
                BigDecimal ljj = accumBalance(ab, SubjDirection.DEBIT);   // 累计借
                BigDecimal ljd = accumBalance(ab, SubjDirection.CREDIT);  // 累计贷

//                mergeBigDecimal(aRow, initColi, initBalance);
                boolean is1122 = subjCode.equals("1122");
                mergeBigDecimal(aRow, ljjColI, ljj);
                mergeBigDecimal(aRow, ljdColI, is1122 ? ljd : ljj);

                if (is1122) {
                    mergeBigDecimal(aRow, initColi, initBalance);
                    // 1122有2020年度的值，2203没有
                    BigDecimal y2020 = special(context, deptKey, Y2020_2);
                    mergeBigDecimal(aRow, y2020Coli, y2020);
                }

            }
        }

        int rowI = 1;
        String beforeSubj = null;   // 之前的科目
        String beforeName = null;   // 之前的科目名称
        Map<Integer, BigDecimal> countMap = new HashMap<>();
        for (Map.Entry<String, Map<Integer, BigDecimal>> entry : aRowBalanceMap.entrySet()) {
            String[] split = entry.getKey().split("_");
            if (isEmpty(entry.getValue())) continue;
            String deptCode = split[0];
            String deptName = split[1];
            String subjName = split[2];
            String subjCode = split[3];
            if (beforeSubj == null) {
                beforeSubj = subjCode;
                beforeName = subjName;
            }

            int ri = rowI;
            if (!beforeSubj.equals(subjCode)) {
                addCellWriter(result, ri, dCodeColi, "合计");
                addCellWriter(result, ri, dNameColi, beforeName + "合计");
                addCellWriter(result, ri, subjColi, beforeName);
                countMap.forEach((colI, val) -> addCellWriter(result, ri, colI, val));
                countMap.clear();
                rowI++;
                beforeSubj = subjCode;
                beforeName = subjName;
            }

            addCellWriter(result, rowI, dCodeColi, deptCode);
            addCellWriter(result, rowI, dNameColi, deptName);
            addCellWriter(result, rowI, subjColi, subjName);

            int rrI = rowI;
            entry.getValue().forEach((colI, val) -> {
                addCellWriter(result, rrI, colI, val);
                mergeBigDecimal(countMap, colI, val);
            });
            rowI++;
        }
        // 最后一个科目的合计不会处理，需要补上
        if (!countMap.isEmpty()) {
            addCellWriter(result, rowI, dCodeColi, "合计");
            addCellWriter(result, rowI, dNameColi, beforeName + "合计");
            addCellWriter(result, rowI, subjColi, beforeName);
            int r = rowI;
            countMap.forEach((colI, val) -> addCellWriter(result, r, colI, val));
            countMap.clear();
        }
    }

    /**
     * 从数据模板中取值
     * 包括2020年度的值和2025年度的期初数
     * @param context
     * @param key
     * @param colKey
     * @return
     */
    private BigDecimal special(StmtGenContext context, String key, String colKey) {
        Map<String, Map<String, String>> mapData = context.rcHeadMapData();
        Map<String, String> aRow = mapData.get(key);
        if (MapUtils.isEmpty(aRow)) return null;
        String s = aRow.get(colKey);
        if (StringUtils.isEmpty(s)) return null;
        return new BigDecimal(s);
    }

    /**
     * 从行列头映射数据集中提取出key集合，然后转换为部门编码_部门名称集合
     * @param context
     * @return
     */
    private Set<String> getDeptKeySet(StmtGenContext context) {
        Map<String, Map<String, String>> mapData = context.rcHeadMapData();
        if (MapUtils.isEmpty(mapData)) return Collections.emptySet();
        Set<String> keys = mapData.keySet();
        return new HashSet<>(keys);
    }

    /**
     * 以当前年度上年的1-12月的辅助余额列表计算最终的期初数
     * 建立 部门-最终期初数 映射
     * 最终的期初数计算方式：期初数+累计借方-累计贷方
     * @param lyList
     * @return
     */
    private Map<String, BigDecimal> initBalanceMap(List<AuxBalance> lyList) {
        if (CollectionUtils.isEmpty(lyList)) return Collections.emptyMap();
        Map<String, BigDecimal> map = new HashMap<>();
        for (AuxBalance ab : lyList) {
            List<AuxAcctProject> aapList = ab.getGlqueryassvo();
            if (CollectionUtils.isEmpty(aapList)) continue;
            String deptCode = aapList.get(0).getAsscode();
            String subjCode = ab.getPk_accsubj_code();
            BigDecimal init = initBalance(ab, subjDirection(subjCode));
            BigDecimal ljj = accumBalance(ab, SubjDirection.DEBIT); // 累计借
            BigDecimal ljd = accumBalance(ab, SubjDirection.CREDIT);    // 累计贷
            BigDecimal subtract = bdSubtract(ljj, ljd); // 累计差额
            BigDecimal fVal = bdAdd(init, subtract);
            map.put(deptCode, fVal);
        }
        return map;
    }

    private boolean isEmpty(Map<?, BigDecimal> map) {
        return map == null || map.isEmpty() ||
                map.values().stream().noneMatch(Objects::nonNull);
    }
}
