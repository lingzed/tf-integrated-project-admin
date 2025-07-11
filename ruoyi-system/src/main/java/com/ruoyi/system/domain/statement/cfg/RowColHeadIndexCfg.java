package com.ruoyi.system.domain.statement.cfg;

import java.util.List;

/**
 * 建立行列头索引映射的相关配置
 */
public class RowColHeadIndexCfg {
    private String sheetKey;                    // 工作表键
    private Integer sheetIndex;                 // 工作表索引
    private List<RowHeadCfg> rowHeadCfgList;    // 行头配置
    private List<ColHeadCfg> colHeadCfgList;    // 列头配置

    public String getSheetKey() {
        return sheetKey;
    }

    public void setSheetKey(String sheetKey) {
        this.sheetKey = sheetKey;
    }

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
