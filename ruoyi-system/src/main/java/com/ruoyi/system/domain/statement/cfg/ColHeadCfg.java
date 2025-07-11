package com.ruoyi.system.domain.statement.cfg;

import java.util.List;
import java.util.Map;

/**
 * 列头配置
 */
public class ColHeadCfg {
    private Integer colHdStartRowI;             // 列头开始行 索引
    private Integer colHdStartColI;             // 列头开始列 索引
    private List<Integer> colHdRowArea;         // 列头区域，开始行 和 结束行 索引
    private List<Integer> colHdColArea;         // 列头区域，开始列 和 结束列 索引
    private Integer cValProcessMtd;             // 列头原值处理值方式，0: 不处理, 1: 从括号中获取, 2: 获取-之前的值
    private Map<String, String> colHdConvertMapper;    // 列头转换映射，对cValProcessMtd处理之后的值进行转换，将键转换为映射的键
    private Boolean enablePrefix;               // 是否启用前缀
    private Integer offset;                     // 数据与头索引的偏移
    private List<Integer> colHdStartColIndexList;    // 列头开始列的索引集合，这不是一个范围而是一个索引集合
    private String connector;                   // 连接符，当读取行列头对应的数据时，若以 colHdColIndexArea中列索引对应列的值 为键，则每列值之间以 connector 连接


    public Integer getColHdStartRowI() {
        return colHdStartRowI;
    }

    public void setColHdStartRowI(Integer colHdStartRowI) {
        this.colHdStartRowI = colHdStartRowI;
    }

    public Integer getColHdStartColI() {
        return colHdStartColI;
    }

    public void setColHdStartColI(Integer colHdStartColI) {
        this.colHdStartColI = colHdStartColI;
    }

    public List<Integer> getColHdRowArea() {
        return colHdRowArea;
    }

    public void setColHdRowArea(List<Integer> colHdRowArea) {
        this.colHdRowArea = colHdRowArea;
    }

    public List<Integer> getColHdColArea() {
        return colHdColArea;
    }

    public void setColHdColArea(List<Integer> colHdColArea) {
        this.colHdColArea = colHdColArea;
    }

    public Integer getcValProcessMtd() {
        return cValProcessMtd;
    }

    public void setcValProcessMtd(Integer cValProcessMtd) {
        this.cValProcessMtd = cValProcessMtd;
    }

    public Map<String, String> getColHdConvertMapper() {
        return colHdConvertMapper;
    }

    public void setColHdConvertMapper(Map<String, String> colHdConvertMapper) {
        this.colHdConvertMapper = colHdConvertMapper;
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

    public List<Integer> getColHdStartColIndexList() {
        return colHdStartColIndexList;
    }

    public void setColHdStartColIndexList(List<Integer> colHdStartColIndexList) {
        this.colHdStartColIndexList = colHdStartColIndexList;
    }

    public String getConnector() {
        if (connector == null) return "";
        return connector;
    }

    public void setConnector(String connector) {
        this.connector = connector;
    }
}
