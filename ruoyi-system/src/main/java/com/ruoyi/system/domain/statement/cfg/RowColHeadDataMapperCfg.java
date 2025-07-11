package com.ruoyi.system.domain.statement.cfg;

import java.util.List;

/**
 * 建立行列头映射数据集相关的配置
 */
public class RowColHeadDataMapperCfg {
    private Integer sheetIndex;    // 工作表索引
    private List<RowHeadCfg> rowHeadCfgList;    // 列表，因为可能有多个行头(如一行中有多个范围列是头，再比如多行中的列范围)
    private List<ColHeadCfg> colHeadCfgList;    // 同理

    public Integer getSheetIndex() {
        return sheetIndex;
    }

    public void setSheetIndex(Integer sheetIndex) {
        this.sheetIndex = sheetIndex;
    }

    public List<RowHeadCfg> getRowHeadCfgList() {
        return rowHeadCfgList;
    }

    public void setRowHeadCfgList(List<RowHeadCfg> rowHeadCfgList) {
        this.rowHeadCfgList = rowHeadCfgList;
    }

    public List<ColHeadCfg> getColHeadCfgList() {
        return colHeadCfgList;
    }

    public void setColHeadCfgList(List<ColHeadCfg> colHeadCfgList) {
        this.colHeadCfgList = colHeadCfgList;
    }
}
