package com.ruoyi.system.domain.statement;

import org.apache.commons.collections4.MapUtils;

import java.util.Map;

/**
 * 行列头索引实体
 */
public class RowColHeadIndex {
    private Map<String, Integer> rowHeadIdx;  // 行头索引映射，其中key为行头单元格的值，value为该单元格列索引
    private Map<String, Integer> colHeadIdx;  // 列头索引映射，其中key为列头单元格的值，value为该单元格行索引

    public Map<String, Integer> getRowHeadIdx() {
        return rowHeadIdx;
    }

    public void setRowHeadIdx(Map<String, Integer> rowHeadIdx) {
        this.rowHeadIdx = rowHeadIdx;
    }

    public Map<String, Integer> getColHeadIdx() {
        return colHeadIdx;
    }

    public void setColHeadIdx(Map<String, Integer> colHeadIdx) {
        this.colHeadIdx = colHeadIdx;
    }
}
