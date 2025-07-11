package com.ruoyi.system.domain.statement.cfg;

import java.util.List;
import java.util.Set;

/**
 * 报表查询的相关配置
 */
public class StatementQueryCfg {
    private String corpCode;            // 此查询配置属于哪个公司的编码
    private List<String> queryCorpList; // 查询的公司编码列表
    private List<String> queryCostList; // 查询的客商编码列表
    private Set<String> querySubjList;  // 查询的科目编码列表
    private List<String> queryDeptList; // 查询的部门编码列表
    private List<String> rowHdQueryRemove; // 用行头中的值作为参数去查询时，需要移除的值
    private List<String> colHdQueryRemove; // 用列头中的值作为参数去查询时，需要移除的值
    private String subjCodeFrom;            // 科目编码从
    private String subjCodeTo;              // 科目编码至

    public String getCorpCode() {
        return corpCode;
    }

    public void setCorpCode(String corpCode) {
        this.corpCode = corpCode;
    }

    public List<String> getQueryCorpList() {
        return queryCorpList;
    }

    public void setQueryCorpList(List<String> queryCorpList) {
        this.queryCorpList = queryCorpList;
    }

    public List<String> getQueryCostList() {
        return queryCostList;
    }

    public void setQueryCostList(List<String> queryCostList) {
        this.queryCostList = queryCostList;
    }

    public Set<String> getQuerySubjList() {
        return querySubjList;
    }

    public void setQuerySubjList(Set<String> querySubjList) {
        this.querySubjList = querySubjList;
    }

    public List<String> getQueryDeptList() {
        return queryDeptList;
    }

    public void setQueryDeptList(List<String> queryDeptList) {
        this.queryDeptList = queryDeptList;
    }

    public List<String> getRowHdQueryRemove() {
        return rowHdQueryRemove;
    }

    public void setRowHdQueryRemove(List<String> rowHdQueryRemove) {
        this.rowHdQueryRemove = rowHdQueryRemove;
    }

    public List<String> getColHdQueryRemove() {
        return colHdQueryRemove;
    }

    public void setColHdQueryRemove(List<String> colHdQueryRemove) {
        this.colHdQueryRemove = colHdQueryRemove;
    }

    public String getSubjCodeFrom() {
        return subjCodeFrom;
    }

    public void setSubjCodeFrom(String subjCodeFrom) {
        this.subjCodeFrom = subjCodeFrom;
    }

    public String getSubjCodeTo() {
        return subjCodeTo;
    }

    public void setSubjCodeTo(String subjCodeTo) {
        this.subjCodeTo = subjCodeTo;
    }
}
