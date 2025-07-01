package com.ruoyi.system.domain.statementcfg.vo;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class StatementCfgVo {
	private String cfgId;
	private String cfgCode;
	private String cfgDescription;
	private String corpCode;
	private String statementCode;
	private String statementName;
	private String cfgContent;
	private Short contentJsonType;  // 配置内容的json类型
	private Short cfgType;
	@JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss", timezone = "GMT-8")
	private Date createTime;
	@JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss", timezone = "GMT-8")
	private Date updateTime;

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
}