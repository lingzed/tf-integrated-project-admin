package com.ruoyi.system.service.statement.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.ruoyi.common.constant.MsgConstants;
import com.ruoyi.common.enums.statement.StatementType;
import com.ruoyi.common.enums.u8c.SubjDirection;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.u8c.AuxAcctProject;
import com.ruoyi.common.u8c.AuxBalance;
import com.ruoyi.system.domain.statement.CellWriter;
import com.ruoyi.system.domain.statement.StmtGenContext;
import com.ruoyi.common.u8c.warpper.AuxBalanceDataWrapper;
import com.ruoyi.common.u8c.warpper.FinancialDataWrapper;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.statement.RowColHeadIndex;
import com.ruoyi.system.domain.statement.cfg.SheetWriteCfg;
import com.ruoyi.system.domain.statement.cfg.StatementQueryCfg;
import com.ruoyi.system.domain.statement.cfg.StmtCfgWrapper;
import com.ruoyi.system.service.statement.BaseStmtExtractor;
import com.ruoyi.system.service.statement.ValueExtractor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

/**
 * 部门资金利息测试表数据提取器
 */
@Component
public class BmZjLxCsStmtExtractor extends BaseStmtExtractor implements ValueExtractor<BigDecimal> {
    private static final Logger log = LoggerFactory.getLogger(BmZjLxCsStmtExtractor.class);

    @Override
    public List<CellWriter<BigDecimal>> extract(FinancialDataWrapper data, RowColHeadIndex rcHeadIndex, StmtCfgWrapper stmtCfg,
                                                Integer index, StmtGenContext context) {
        List<CellWriter<BigDecimal>> result = new ArrayList<>();

        // 数据模板行列头映射数据集
        Map<String, Map<String, String>> rcHeadMapData = context.rcHeadMapData();
        if (MapUtils.isEmpty(rcHeadMapData)) {
            throw new ServiceException(MsgConstants.RC_HEAD_MAP_DATA_IS_EMPTY);
        }

        List<AuxBalance> abList = reviewData(data);     // 审查财务数据
        if (CollectionUtils.isEmpty(abList)) return result;

        List<SheetWriteCfg> writeCfgList = reviewCfg(stmtCfg);  // 审查写入配置
        if (CollectionUtils.isEmpty(writeCfgList)) return result;

        List<String> recode = new ArrayList<>();
        for (AuxBalance ab : abList) {
            String subjCode = ab.getPk_accsubj_code();  // 科目id
            List<AuxAcctProject> aaProjList = ab.getGlqueryassvo();  // 辅助核算项目集合
            if (CollectionUtils.isEmpty(aaProjList)) continue;
            String deptId = aaProjList.get(0).getAsscode();

            // 获得坐标
            int ri = rowIndex(rcHeadIndex, subjCode);
            int ci = colIndex(rcHeadIndex, deptId);
            if (ri < 0 || ci < 0) continue;
            recode.add(subjCode + "_" + deptId);

            Map<String, String> aRow = rcHeadMapData.get(subjCode);
            if (MapUtils.isEmpty(aRow)) {
                log.warn("未找到行头键 【{}】 在报表 【{}】 的行列头映射数据中的对应项", subjCode, StatementType.BM_ZJ_LX_CSB.getStatementName());
                continue;
            }

            String originalStr = aRow.get(deptId);
            BigDecimal original = StringUtils.isEmpty(originalStr) ? null : new BigDecimal(originalStr);
            BigDecimal ljj = accumBalance(ab, SubjDirection.DEBIT);    // 累计借
            BigDecimal ljd = accumBalance(ab, SubjDirection.CREDIT);    // 累计贷
            // 1开头为借-贷，2开头为贷-借
            BigDecimal subtract = subjCode.startsWith("1") ? bdSubtract(ljj, ljd) : bdSubtract(ljd, ljj);
            BigDecimal val = original == null ? subtract : subtract == null ? original : original.add(subtract);
            BigDecimal fVal = process(deptId, subjCode, val, stmtCfg, context, index);

            addCellWriter(result, ri, ci + 1, fVal);
        }

        rcHeadMapData.forEach((subjCode, map) -> map.forEach((deptCode, val) -> {
            String r = subjCode + "_" + deptCode;
            if (recode.contains(r)) return;

            int ri = rowIndex(rcHeadIndex, subjCode);
            int ci = colIndex(rcHeadIndex, deptCode);
            if (ri < 0 || ci < 0) return;
            addCellWriter(result, ri, ci + 1, StringUtils.isEmpty(val) ? null : new BigDecimal(val));
        }));

        return result;
    }

    private List<AuxBalance> reviewData(FinancialDataWrapper data) {
        return Optional.ofNullable(data)
                .map(FinancialDataWrapper::getAuxBalanceDataWrapper)
                .map(AuxBalanceDataWrapper::getCurrPeriod)
                .orElse(null);
    }

    private List<SheetWriteCfg> reviewCfg(StmtCfgWrapper stmtCfg) {
        return Optional.ofNullable(stmtCfg)
                .map(StmtCfgWrapper::getListWriteCfg)
                .orElse(null);
    }

    private BigDecimal process(String deptCode, String subjCode, BigDecimal original, StmtCfgWrapper stmtCfg,
                               StmtGenContext context, Integer index) {
        // 获取查询配置
        StatementQueryCfg queryCfg = stmtCfg.getObjQueryCfg();
        if (queryCfg == null) return original;

        List<String> queryDeptList = queryCfg.getQueryDeptList();
        Set<String> querySubjList = queryCfg.getQuerySubjList();
        if (CollectionUtils.isEmpty(queryDeptList) || !queryDeptList.contains(deptCode)
                || CollectionUtils.isEmpty(querySubjList) || !querySubjList.contains(subjCode)) return original;

        Map<String, FinancialDataWrapper> spData = context.get(BmZjLxCsStmtGen.SUPPLEMENT,
                new TypeReference<Map<String, FinancialDataWrapper>>() {
                });     // 补充查询的数据集
        if (MapUtils.isEmpty(spData)) return original;
        List<SheetWriteCfg> sheetWriteCfg = reviewCfg(stmtCfg);
        if (CollectionUtils.isEmpty(sheetWriteCfg)) return original;
        String key = sheetWriteCfg.get(index).getSheetKey();
        List<AuxBalance> aaList = reviewData(spData.get(key));
        if (CollectionUtils.isEmpty(aaList)) {
            log.warn("补充查询数据集中, 键【{}】对应的数据为null", key);
            return original;
        }
        BigDecimal count = BigDecimal.ZERO;
        for (AuxBalance aa : aaList) {
            List<AuxAcctProject> aapList = aa.getGlqueryassvo();
            if (CollectionUtils.isNotEmpty(aapList)) continue;

            BigDecimal ljj = accumBalance(aa, SubjDirection.DEBIT);    // 累计借
            BigDecimal ljd = accumBalance(aa, SubjDirection.CREDIT);    // 累计贷
            count = count.add(bdSubtract(ljj, ljd));
        }
        return original.subtract(count);
    }
}
