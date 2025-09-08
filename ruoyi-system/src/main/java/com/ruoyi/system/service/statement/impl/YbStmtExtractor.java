package com.ruoyi.system.service.statement.impl;

import com.ruoyi.common.u8c.warpper.FinancialDataWrapper;
import com.ruoyi.common.utils.spring.SpringUtils;
import com.ruoyi.system.domain.statement.CellWriter;
import com.ruoyi.system.domain.statement.RowColHeadIndex;
import com.ruoyi.system.domain.statement.StmtGenContext;
import com.ruoyi.system.domain.statement.cfg.StatementQueryCfg;
import com.ruoyi.system.domain.statement.cfg.StmtCfgWrapper;
import com.ruoyi.system.service.statement.ValueExtractor;
import com.ruoyi.system.service.statement.impl.extractor.ExtractorHandle;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 月报提取器
 */
@Component
public class YbStmtExtractor implements ValueExtractor<Object> {

    @Override
    public List<CellWriter<Object>> extract(FinancialDataWrapper data, RowColHeadIndex rcHeadIndex, StmtCfgWrapper stmtCfg,
                                            Integer index, StmtGenContext context) {
        List<CellWriter<Object>> result = new ArrayList<>();
        String sheetKey = stmtCfg.getListWriteCfg().get(index).getSheetKey();
        StatementQueryCfg queryCfg = stmtCfg.getMapQueryCfg().get("commonCorpList");
        List<String> corpList = Optional.ofNullable(queryCfg).map(StatementQueryCfg::getQueryCorpList).orElse(null);

        ExtractorHandle extractorHandle = SpringUtils.getBean(sheetKey);
        extractorHandle.handle(result, data, rcHeadIndex, corpList, context);

        return result;
    }
}
