package com.ruoyi.system.service.statement.impl;

import com.ruoyi.common.enums.u8c.SubjDirection;
import com.ruoyi.common.u8c.AuxAcctProject;
import com.ruoyi.common.u8c.AuxBalance;
import com.ruoyi.common.u8c.warpper.FinancialDataWrapper;
import com.ruoyi.system.domain.statement.CellWriter;
import com.ruoyi.system.domain.statement.RowColHeadIndex;
import com.ruoyi.system.domain.statement.StmtGenContext;
import com.ruoyi.system.domain.statement.cfg.SheetWriteCfg;
import com.ruoyi.system.domain.statement.cfg.StmtCfgWrapper;
import com.ruoyi.system.service.statement.BaseStmtExtractor;
import com.ruoyi.system.service.statement.ValueExtractor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 各专业公司各省份收入情况统计表提取器
 */
@Component
public class GgsGsfSrQkTjStmtExtractor extends BaseStmtExtractor implements ValueExtractor<Object> {
    @Override
    public List<CellWriter<Object>> extract(FinancialDataWrapper data, RowColHeadIndex rcHeadIndex,
                                            StmtCfgWrapper stmtCfg, Integer index, StmtGenContext context) {
        List<CellWriter<Object>> result = new ArrayList<>();

        List<SheetWriteCfg> listWriteCfg = stmtCfg.getListWriteCfg();
        String sheetKey = listWriteCfg.get(index).getSheetKey();
        int year = context.periodYear();

        List<AuxBalance> curr = data.getAuxBalanceDataWrapper().getCurrPeriod();    // 本期
        List<AuxBalance> pre = data.getAuxBalanceDataWrapper().getPreYearSamePeriod();  // 上年同期

        loopProcess(result, curr, rcHeadIndex, sheetKey, 1, year);   // 当前在后，上年在前
        loopProcess(result, pre, rcHeadIndex, sheetKey, 0, year);
        return result;
    }

    private void loopProcess(List<CellWriter<Object>> result, List<AuxBalance> abList, RowColHeadIndex rcHeadIndex,
                             String sheetKey, int offset, int year) {
        if (CollectionUtils.isEmpty(abList)) return;
        abList.forEach(ab -> {
            List<AuxAcctProject> aapList = ab.getGlqueryassvo();
            if (CollectionUtils.isEmpty(aapList)) return;

            String hyCode = aapList.get(0).getAsscode();    // 行业类型编码
            String subjCode = ab.getPk_accsubj_code();      // 科目编码
            boolean byDept = aapList.size() == 2;  // 是否按部门
            String colKey = byDept ? subjCode + "_" + aapList.get(1).getAsscode() : subjCode;

            int colI = colIndex(rcHeadIndex, colKey) + offset;
            int rowI = rowIndex(rcHeadIndex, hyCode);
            if (rowI < 0 || colI < 0) return;

            BigDecimal ljd = accumBalance(ab, SubjDirection.CREDIT); // 累计贷方
            addCellWriter(result, rowI, colI, ljd);
            if (sheetKey.equals("A0101")) {
                addCellWriter(result, 0, 0, (year - 1) + "年");    // 上年
                addCellWriter(result, 0, 1, year + "年");    // 本年
            }
        });
    }
}
