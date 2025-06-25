package com.ruoyi.system.domain.statementcfg;

import java.util.List;
import java.util.Map;

/**
 * 行头配置
 */
public class RowHeadCfg {
    private Integer rowHdStartRowI;         // 行头开始行 索引
    private Integer rowHdStartColI;         // 行头开始列 索引
    private List<Integer> rowHdRowArea;     // 行头区域，开始行 和 结束行 的索引
    private List<Integer> rowHdColArea;     // 行头区域，开始列 和 结束列 的索引
    private Integer rValProcessMtd;         // 行头原值处理值方式，0: 不处理, 1: 从括号中获取, 2: 获取-之前的值
    private Map<String, String> rowHdConvertMapper;    // 行头转换映射，对rValProcessMtd处理之后的值进行转换，键转换为映射的键
    private Boolean enablePrefix;           // 是否启用前缀
    private Integer offset;                 // 数据与头索引的偏移，如头索引为2，偏移为1，本该取2对应的值，则现在会取2+1=3对应的值

    public Integer getRowHdStartRowI() {
        return rowHdStartRowI;
    }

    public void setRowHdStartRowI(Integer rowHdStartRowI) {
        this.rowHdStartRowI = rowHdStartRowI;
    }

    public Integer getRowHdStartColI() {
        return rowHdStartColI;
    }

    public void setRowHdStartColI(Integer rowHdStartColI) {
        this.rowHdStartColI = rowHdStartColI;
    }

    public List<Integer> getRowHdRowArea() {
        return rowHdRowArea;
    }

    public void setRowHdRowArea(List<Integer> rowHdRowArea) {
        this.rowHdRowArea = rowHdRowArea;
    }

    public List<Integer> getRowHdColArea() {
        return rowHdColArea;
    }

    public void setRowHdColArea(List<Integer> rowHdColArea) {
        this.rowHdColArea = rowHdColArea;
    }

    public Integer getrValProcessMtd() {
        return rValProcessMtd;
    }

    public void setrValProcessMtd(Integer rValProcessMtd) {
        this.rValProcessMtd = rValProcessMtd;
    }

    public Map<String, String> getRowHdConvertMapper() {
        return rowHdConvertMapper;
    }

    public void setRowHdConvertMapper(Map<String, String> rowHdConvertMapper) {
        this.rowHdConvertMapper = rowHdConvertMapper;
    }

    public Boolean getEnablePrefix() {
        return enablePrefix;
    }

    public void setEnablePrefix(Boolean enablePrefix) {
        this.enablePrefix = enablePrefix;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }
}
