package com.ruoyi.system.domain.statement.po;

import java.util.Date;

/**
 * 报表生成的相关配置
 */
public class StatementCfg {
    private String cfgId;   // 配置项的id
    private String cfgCode; // 配置的编码, 适配redis的key, 格式为 cfg:报表编码:公司编码:配置类型[:其他信息]
    private String cfgDescription;  // 配置项的描述
    private String corpCode;    // 配置项所属的公司编码
    private String statementCode;   // 配置项对应的报表的编码
    private String statementName;   // 配置项对应的报表的名称
    private String cfgContent;   // 配置项的json内容
    private Short contentJsonType;  // 配置内容的json类型
    private Short cfgType;    // 配置项的类型, 0: 行列头索引配置, 1: 工作表写入配置, 2: 查询配置, 3: 行列头映射数据集配置
    private Date createTime;    // 创建时间
    private Date updateTime;    // 更新时间
    private Integer lockVersion;    // 乐观锁

    public String getCfgId() {
        return cfgId;
    }

    public void setCfgId(String cfgId) {
        this.cfgId = cfgId;
    }

    public String getCfgCode() {
        return cfgCode;
    }

    public void setCfgCode(String cfgCode) {
        this.cfgCode = cfgCode;
    }

    public String getCfgDescription() {
        return cfgDescription;
    }

    public void setCfgDescription(String cfgDescription) {
        this.cfgDescription = cfgDescription;
    }

    public String getCorpCode() {
        return corpCode;
    }

    public void setCorpCode(String corpCode) {
        this.corpCode = corpCode;
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

    public String getCfgContent() {
        return cfgContent;
    }

    public void setCfgContent(String cfgContent) {
        this.cfgContent = cfgContent;
    }

    public Short getContentJsonType() {
        return contentJsonType;
    }

    public void setContentJsonType(Short contentJsonType) {
        this.contentJsonType = contentJsonType;
    }

    public Short getCfgType() {
        return cfgType;
    }

    public void setCfgType(Short cfgType) {
        this.cfgType = cfgType;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getLockVersion() {
        return lockVersion;
    }

    public void setLockVersion(Integer lockVersion) {
        this.lockVersion = lockVersion;
    }
}
