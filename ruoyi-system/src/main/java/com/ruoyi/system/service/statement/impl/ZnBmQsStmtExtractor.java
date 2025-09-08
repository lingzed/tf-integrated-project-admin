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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 职能部门取数表提取器
 */
@Component
public class ZnBmQsStmtExtractor extends BaseStmtExtractor implements ValueExtractor<BigDecimal> {
    private static final Logger log = LoggerFactory.getLogger(ZnBmQsStmtExtractor.class);

    @Override
    public List<CellWriter<BigDecimal>> extract(FinancialDataWrapper data, RowColHeadIndex rcHeadIndex,
                                                StmtCfgWrapper stmtCfg, Integer index, StmtGenContext context) {

        List<CellWriter<BigDecimal>> result = new ArrayList<>();
        List<AuxBalance> aaList = data.getAuxBalanceDataWrapper().getCurrPeriod();
        if (CollectionUtils.isEmpty(aaList)) return result;
        SheetWriteCfg writeCfg = stmtCfg.getListWriteCfg().get(index);
        String corp = writeCfg.getSheetKey();
        Integer statType = writeCfg.getStatType();

        for (AuxBalance ab : aaList) {
            String subjCode = ab.getPk_accsubj_code();  // 科目id
            List<AuxAcctProject> aapList = ab.getGlqueryassvo();  // 辅助核算项目集合
            if (CollectionUtils.isEmpty(aapList)) continue;
            String deptCode = aapList.get(0).getAsscode();

            // 获得坐标
            int ri = rowIndex(rcHeadIndex, subjCode);
            int ci = colIndex(rcHeadIndex, deptCode);

            // 得到本月和累计数
            SubjDirection subjDirection = getSubjDirection(subjCode, corp);
            BigDecimal bqs = currBalance(ab, subjDirection);
            BigDecimal ljs = accumBalance(ab, subjDirection);

            // 根据统计类型得到本月数或累计数，将其封装到CellWrite中，然后装入集合
            if (statType.equals(0)) {   // 本月数
                addCellWriter(result, ri, ci, bqs);
            } else if (statType.equals(1)) {// 本年累计
                addCellWriter(result, ri, ci, ljs);
            } else if (statType.equals(2)) {
                addCellWriter(result, ri, ci, bqs);
                addCellWriter(result, ri, ci + 1, ljs);
            } else {
                log.warn("没有这个统计类型：{}", statType);
            }
        }

        return result;
    }

    private SubjDirection getSubjDirection(String subjCode, String corp) {
        List<String> list = Arrays.asList("A05", "A01", "A02", "A03");
        if ("221102".equals(subjCode) && list.contains(corp.substring(0, 3))) {
            return SubjDirection.DEBIT;
        } else {
            return subjDirection(subjCode);
        }
    }
}
