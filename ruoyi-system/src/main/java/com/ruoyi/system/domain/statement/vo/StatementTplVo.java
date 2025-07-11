package com.ruoyi.system.domain.statement.vo;

/**
 * 报表模板文件vo
 */
public class StatementTplVo {
    private Integer tplId;  // 报表模板id
    private String statementCode;   // 报表类型编码
    private String statementName;   // 报表的名称
    private String corpCode;    // 公司编码
    private String tplFilename; // 模板文件名称
    private String desc;    // 描述

    public Integer getTplId() {
        return tplId;
    }

    public void setTplId(Integer tplId) {
        this.tplId = tplId;
    }

    public String getStatementCode() {
        return statementCode;
    }

    public void setStatementCode(String statementCode) {
        this.statementCode = statementCode;
    }

    public String getStatementName() {
        return statementName;
    }

    public void setStatementName(String statementName) {
        this.statementName = statementName;
    }

    public String getCorpCode() {
        return corpCode;
    }

    public void setCorpCode(String corpCode) {
        this.corpCode = corpCode;
    }

    public String getTplFilename() {
        return tplFilename;
    }

    public void setTplFilename(String tplFilename) {
        this.tplFilename = tplFilename;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
