package com.ruoyi.system.domain.statement.query;

/**
 * 报表配置查询实体
 */
public class StatementCfgQuery extends BaseQuery {
	private String cfgCode;
	private String cfgDescription;
	private String corpCode;
	private String statementCode;
	private String statementName;
	private String cfgContent;
	private Short cfgType;

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

	public Short getCfgType() {
		return cfgType;
	}

	public void setCfgType(Short cfgType) {
		this.cfgType = cfgType;
	}
}