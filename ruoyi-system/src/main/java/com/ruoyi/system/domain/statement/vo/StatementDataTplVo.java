package com.ruoyi.system.domain.statement.vo;

/**
 * 报表数据模板vo
 */
public class StatementDataTplVo {
    private String statementCode;   // 报表类型编码
    private String tplFilename; // 模板文件名称

    public String getStatementCode() {
        return statementCode;
    }

    public void setStatementCode(String statementCode) {
        this.statementCode = statementCode;
    }

    public String getTplFilename() {
        return tplFilename;
    }

    public void setTplFilename(String tplFilename) {
        this.tplFilename = tplFilename;
    }
}
