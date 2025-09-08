package com.ruoyi.system.domain.statement.cfg;

import java.util.List;
import java.util.Set;

/**
 * 工作表写入的相关配置
 */
public class SheetWriteCfg {
    private String sheetKey;                    // 工作表键
    private Integer sheetIndex;                 // 工作表索引
    private Integer statType;                   // 统计类型，0: 本月数, 1: 本年累计, 2: 本月数和本年累计一起
    private Set<String> pSubjList;         //  父级科目列表，因为科目余额查询只会返回下级科目，这个父级科目列表就是用来遍历汇总的
    private List<PeriodWriteCfg> periodCfgList;   // 期间配置列表

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

    public Integer getStatType() {
        return statType;
    }

    public void setStatType(Integer statType) {
        this.statType = statType;
    }

    public Set<String> getpSubjList() {
        return pSubjList;
    }

    public void setpSubjList(Set<String> pSubjList) {
        this.pSubjList = pSubjList;
    }

    public List<PeriodWriteCfg> getPeriodCfgList() {
        return periodCfgList;
    }

    public void setPeriodCfgList(List<PeriodWriteCfg> periodCfgList) {
        this.periodCfgList = periodCfgList;
    }
}
