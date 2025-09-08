package com.ruoyi.system.service.statement.impl.extractor;

import com.alibaba.fastjson2.JSON;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.enums.u8c.SubjDirection;
import com.ruoyi.common.u8c.AuxAcctProject;
import com.ruoyi.common.u8c.AuxBalance;
import com.ruoyi.common.u8c.SubjBalance;
import com.ruoyi.common.u8c.warpper.AuxBalanceDataWrapper;
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

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 应交税费sheet提取逻辑
 */
@Component("YJSF")
public class YjSfExtractorHandle extends BaseStmtExtractor implements ExtractorHandle {
    private static final String YCYE = "年初余额";
    private static final String BQZJ = "本期增加";
    private static final String QMYE = "期末余额";
    private static final Logger log = LoggerFactory.getLogger(YjSfExtractorHandle.class);
    @Resource
    private RedisCache redisCache;

    @Override
    public void handle(List<CellWriter<Object>> result, FinancialDataWrapper data, RowColHeadIndex rcHeadIndex,
                       List<String> corpList, StmtGenContext context) {
        SubjBalanceDataWrapper subjBalanceDataWrapper = data.getSubjBalanceDataWrapper();
        AuxBalanceDataWrapper auxBalanceDataWrapper = data.getAuxBalanceDataWrapper();
        Map<String, Map<String, BigDecimal>> bCountMap = new HashMap<>();   // 余额总计
        boolean isA05 = context.corpCode().equals("A05");
        corpList.forEach(corp -> {
            List<SubjBalance> sbList = subjBalanceDataWrapper.getByCorpCode(corp);
            if (CollectionUtils.isNotEmpty(sbList)) {
                sbList.forEach(sb -> {
                    String subjCode = sb.getPk_accsubj_code();
                    Map<String, BigDecimal> aRow = bCountMap.computeIfAbsent(subjCode, k -> new HashMap<>());
                    SubjDirection direction = subjDirection(subjCode);
                    BigDecimal init = initBalance(sb, direction); // 期初余额
                    BigDecimal bjs = currBalance(sb, direction); // 本期数
                    BigDecimal end = subtractEndBalance(sb, subjCode);  // 期末余额
                    boolean is22210101 = subjCode.startsWith("22210101");
                    mergeBigDecimal(aRow, YCYE, is22210101 ? init.negate() : init);
                    mergeBigDecimal(aRow, BQZJ, is22210101 ? null : bjs);
                    mergeBigDecimal(aRow, QMYE, is22210101 ? end.negate() : end);
                });
            }

            // 辅助余额
            List<AuxBalance> abList = auxBalanceDataWrapper.getByCorpCode(corp);
            if (CollectionUtils.isNotEmpty(abList)) {
                Map<String, Map<String, BigDecimal>> temp = new HashMap<>();

                abList.forEach(ab -> {
                    List<AuxAcctProject> aapList = ab.getGlqueryassvo();
                    if (CollectionUtils.isEmpty(aapList)) return;
                    String subjCode = ab.getPk_accsubj_code();
                    SubjDirection direction = subjDirection(subjCode);
                    BigDecimal init = initBalance(ab, direction); // 期初余额
                    BigDecimal bjs = currBalance(ab, direction); // 本期数
                    BigDecimal end = endBalance(ab, direction); // 期末余额
                    String assCode = aapList.get(0).getAsscode();
                    Map<String, BigDecimal> tempRow = temp.computeIfAbsent(assCode, k -> new HashMap<>());
                    mergeBigDecimal(tempRow, YCYE, init);
                    mergeBigDecimal(tempRow, BQZJ, bjs);
                    mergeBigDecimal(tempRow, QMYE, end);
                });

                BigDecimal[] a17 = getPeriodData(temp, "01");   // 17%
                BigDecimal[] a16 = getPeriodData(temp, "02");   // 16%
                BigDecimal[] a13 = getPeriodData(temp, "07");   // 13%
                BigDecimal[] a9 = getPeriodData(temp, "13");   // 9%
                BigDecimal[] a10 = getPeriodData(temp, "12");   // 10%
                BigDecimal[] a11 = getPeriodData(temp, "03");   // 11%
                BigDecimal[] a6 = getPeriodData(temp, "08");   // 6%
                BigDecimal[] a3 = getPeriodData(temp, "09");   // 3%
                BigDecimal[] a1 = getPeriodData(temp, "10");   // 1%
                BigDecimal[] a5 = getPeriodData(temp, "11");   // 5%

                BigDecimal[] aRowData1 = getARowData(a17, a16, a13);
                BigDecimal[] aRowData2 = getARowData(a9, a10, a11);
                BigDecimal[] aRowData3 = getARowData(a6);
                BigDecimal[] aRowData4 = getARowData(a3, a1);
                BigDecimal[] aRowData5 = getARowData(a5);

                Map<String, BigDecimal> aRow1 = bCountMap.computeIfAbsent("17%+16%+13%", k -> new HashMap<>());
                Map<String, BigDecimal> aRow2 = bCountMap.computeIfAbsent("9%+10%+11%", k -> new HashMap<>());
                Map<String, BigDecimal> aRow3 = bCountMap.computeIfAbsent("6%", k -> new HashMap<>());
                Map<String, BigDecimal> aRow4 = bCountMap.computeIfAbsent("3%+1%", k -> new HashMap<>());
                Map<String, BigDecimal> aRow5 = bCountMap.computeIfAbsent("5%", k -> new HashMap<>());

                add(aRow1, aRowData1);
                add(aRow2, aRowData2);
                add(aRow3, aRowData3);
                add(aRow4, aRowData4);
                add(aRow5, aRowData5);

                if (isA05) {
                    Map<String, BigDecimal> aRow6 = bCountMap.computeIfAbsent("11%", k -> new HashMap<>());
                    Map<String, BigDecimal> aRow7 = bCountMap.computeIfAbsent("13%", k -> new HashMap<>());
                    Map<String, BigDecimal> aRow8 = bCountMap.computeIfAbsent("17%", k -> new HashMap<>());
                    Map<String, BigDecimal> aRow9 = bCountMap.computeIfAbsent("16%", k -> new HashMap<>());
                    Map<String, BigDecimal> aRow10 = bCountMap.computeIfAbsent("10%", k -> new HashMap<>());
                    Map<String, BigDecimal> aRow11 = bCountMap.computeIfAbsent("9%", k -> new HashMap<>());

                    BigDecimal[] aRowData6 = getARowData(a11);
                    BigDecimal[] aRowData7 = getARowData(a13);
                    BigDecimal[] aRowData8 = getARowData(a17);
                    BigDecimal[] aRowData9 = getARowData(a16);
                    BigDecimal[] aRowData10 = getARowData(a10);
                    BigDecimal[] aRowData11 = getARowData(a9);

                    add(aRow6, aRowData6);
                    add(aRow7, aRowData7);
                    add(aRow8, aRowData8);
                    add(aRow9, aRowData9);
                    add(aRow10, aRowData10);
                    add(aRow11, aRowData11);
                }
            }
        });

        selfBdMerge(bCountMap, "+", true, "222115", "222116");
        selfBdMerge(bCountMap, "+", true, "222199", "222134");

        if (isA05) {
            // 物业将数据缓存起来，下一个页签需要使用
            String cacheKey = "YJSF:" + context.userId() + ":bCountMap";
            Map<String, Map<String, String>> cahceMap = new HashMap<>();
            bCountMap.forEach((k, v) -> {
                Map<String, String> aRow = cahceMap.computeIfAbsent(k, k1 -> new HashMap<>());
                v.forEach((k1, v1) -> aRow.put(k1, v1.toString()));
            });
            redisCache.setCacheMap(cacheKey, cahceMap);
            log.info("物业设置【应交税费】缓存成功");
        }

        bCountMap.forEach((rowKey, aRow) -> {
            int ri = rowIndex(rcHeadIndex, rowKey);
            aRow.forEach((colKey, val) -> {
                int ci = colIndex(rcHeadIndex, colKey);
                addCellWriter(result, ri, ci, val);
            });
        });
    }

    private BigDecimal[] getPeriodData(Map<String, Map<String, BigDecimal>> map, String code) {
        BigDecimal init = Optional.ofNullable(map.get(code)).map(m -> m.get(YCYE)).orElse(null);    // 年初余额
        BigDecimal bjs = Optional.ofNullable(map.get(code)).map(m -> m.get(BQZJ)).orElse(null);    // 本期增加
        BigDecimal end = Optional.ofNullable(map.get(code)).map(m -> m.get(QMYE)).orElse(null);    // 期末余额
        return new BigDecimal[]{init, bjs, end};
    }

    private BigDecimal[] getARowData(BigDecimal[]... val) {
        BigDecimal init = BigDecimal.ZERO;
        BigDecimal bjs = BigDecimal.ZERO;
        BigDecimal end = BigDecimal.ZERO;
        for (BigDecimal[] bigDecimals : val) {
            init = bdAdd(init, bigDecimals[0]);
            bjs = bdAdd(bjs, bigDecimals[1]);
            end = bdAdd(end, bigDecimals[2]);
        }
        return new BigDecimal[]{init, bjs, end};
    }

    private void add(Map<String, BigDecimal> aRow, BigDecimal[] aRowData) {
        mergeBigDecimal(aRow, YCYE, aRowData[0]);
        mergeBigDecimal(aRow, BQZJ, aRowData[1]);
        mergeBigDecimal(aRow, QMYE, aRowData[2]);
    }
}
