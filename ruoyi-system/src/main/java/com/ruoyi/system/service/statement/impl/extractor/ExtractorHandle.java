package com.ruoyi.system.service.statement.impl.extractor;

import com.ruoyi.common.u8c.warpper.FinancialDataWrapper;
import com.ruoyi.system.domain.statement.CellWriter;
import com.ruoyi.system.domain.statement.RowColHeadIndex;
import com.ruoyi.system.domain.statement.StmtGenContext;

import java.util.List;

/**
 * 提取器处理<br>
 * 适配不同的sheetKey
 */
public interface ExtractorHandle {
    /**
     * 具体的提取逻辑
     * @param result
     * @param data
     * @param rcHeadIndex
     * @param corpList
     */
    void handle(List<CellWriter<Object>> result, FinancialDataWrapper data, RowColHeadIndex rcHeadIndex,
                List<String> corpList, StmtGenContext context);
}
