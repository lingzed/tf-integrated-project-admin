package com.ruoyi.system.service.statement.impl;

import com.ruoyi.common.enums.u8c.SubjDirection;
import com.ruoyi.common.u8c.AuxAcctProject;
import com.ruoyi.common.u8c.AuxBalance;
import com.ruoyi.common.u8c.warpper.AuxBalanceDataWrapper;
import com.ruoyi.common.u8c.warpper.FinancialDataWrapper;
import com.ruoyi.system.domain.statement.CellWriter;
import com.ruoyi.system.domain.statement.RowColHeadIndex;
import com.ruoyi.system.domain.statement.StmtGenContext;
import com.ruoyi.system.domain.statement.cfg.StmtCfgWrapper;
import com.ruoyi.system.service.statement.BaseStmtExtractor;
import com.ruoyi.system.service.statement.ValueExtractor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

/**
 * 客户余额表提取器
 */
@Component
public class KhYeStmtExtractor extends BaseStmtExtractor implements ValueExtractor<Object> {
    @Override
    public List<CellWriter<Object>> extract(FinancialDataWrapper data, RowColHeadIndex rcHeadIndex,
                                            StmtCfgWrapper stmtCfg, Integer index, StmtGenContext context) {
        List<CellWriter<Object>> result = new ArrayList<>();
        HashMap<String, Map<String, BigDecimal>> kxBalanceMap = new HashMap<>();   // 客商&项目科目余额映射
        Map<String, BigDecimal> sbjBalanceCount = new HashMap<>();  // 科目余额总计

        int ksColI = colIndex(rcHeadIndex, "客户名称");   // 客商列索引
        int xmColI = colIndex(rcHeadIndex, "项目管理");   // 项目列索引
        if (ksColI < 0 || xmColI < 0) return result;

        Map<String, String> costNameCache = new HashMap<>();
        AuxBalanceDataWrapper auxBalanceDataWrapper = data.getAuxBalanceDataWrapper();
        List<AuxBalance> main = auxBalanceDataWrapper.getMain();
        for (AuxBalance ab : main) {
            List<AuxAcctProject> aapList = ab.getGlqueryassvo();
            if (CollectionUtils.isEmpty(aapList)) continue;

            String subjCode = ab.getPk_accsubj_code();
            String costName = aapList.get(0).getAssname();  // 客户名称
            String xmName = aapList.get(1).getAssname();  // 项目名称
            String key = costName + "_" + xmName;

            costNameCache.putIfAbsent(costName, key);   // 缓存第一次客户和key

            // 一行的科目余额
            Map<String, BigDecimal> aRowSbjBalance = kxBalanceMap.computeIfAbsent(key, k -> new HashMap<>());

            // 先拿到科目对应的金额，否则后续会对科目进行更改，用更改后的科目拿不到数据
            BigDecimal val;
            if (subjCode.startsWith("6001060")) {
                val = accumBalance(ab, SubjDirection.CREDIT); // 贷方累计
            } else if (subjCode.equals("1122") || subjCode.equals("1002")) {
                // 接口返回的数据不是最终值，在系统上最终值是用 贷 - 借，所以这里需要算一下最终值
                // 1122 || 1002 是借 - 贷
                val = bdSubtract(ab.getEndDebitLocAmount(), ab.getEndCreditLocAmount());
            } else {
                val = bdSubtract(ab.getEndCreditLocAmount(), ab.getEndDebitLocAmount()).negate();
            }
            mergeBigDecimal(aRowSbjBalance, subjCode, val);
        }

        List<AuxBalance> supplement = auxBalanceDataWrapper.getSupplement();
        for (AuxBalance ab : supplement) {
            List<AuxAcctProject> aapList = ab.getGlqueryassvo();
            if (CollectionUtils.isEmpty(aapList)) continue;

            String costName = aapList.get(0).getAssname(); // 客户名称

            String key = costNameCache.get(costName);   // 第一次客户所属行key
            BigDecimal val = bdSubtract(ab.getEndDebitLocAmount(), ab.getEndCreditLocAmount());
            String subjCode = ab.getPk_accsubj_code();
            if (key == null) {
                // 新增
                Map<String, BigDecimal> aRow = kxBalanceMap.computeIfAbsent(costName, k -> new HashMap<>());
                mergeBigDecimal(aRow, subjCode, val);
            } else {
                // 合并
                mergeBigDecimal(kxBalanceMap.get(key), subjCode, val);
            }
        }
        int rowI = 1;
        Set<String> keySet = kxBalanceMap.keySet();
        for (String kxNameKey : keySet) {

            String[] split = kxNameKey.split("_");
            String ksName = split[0]; // 客商名
            String xmName = kxNameKey.contains("_") ? split[1] : ""; // 项目名

            addCellWriter(result, rowI, ksColI, ksName);
            addCellWriter(result, rowI, xmColI, xmName);

            Map<String, BigDecimal> aRow = kxBalanceMap.get(kxNameKey);
            // 空行跳过
            if (isNullRow(aRow)) continue;

            int r = rowI;
            aRow.forEach((sbjCode, bigDecimal) -> {
                int colI = colIndex(rcHeadIndex, sbjCode);    // 科目列索引
                addCellWriter(result, r, colI, bigDecimal);
                sbjBalanceCount.merge(sbjCode, bigDecimal, BigDecimal::add);   // 统计当前列科目的余额总计数
            });
            rowI++;
        }

        int lastRow = rowI;
        addCellWriter(result, lastRow, 0, lastRow - 1 + "条");
        sbjBalanceCount.forEach((sbjCode, balanceCount) ->
                addCellWriter(result, lastRow, colIndex(rcHeadIndex, sbjCode), balanceCount));
        return result;
    }

    // 客户要求若一行数据全部为0，则不显示
    // 一行当中每个单元格与0进行比较，不能用总和是否为0来表示，因为可能存在两个相反数的情况，这种情况下他们的和为0，但是这行数据不是全部为0
    public boolean isNullRow(Map<String, BigDecimal> aRow) {
        BigDecimal zero = BigDecimal.ZERO;
        for (BigDecimal value : aRow.values()) {
            if (value == null) continue;
            if (zero.compareTo(value) != 0) {
                return false;
            }
        }
        return true;
    }
}
