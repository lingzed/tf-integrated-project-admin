package com.ruoyi.system.domain.statementcfg.vo;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class StatementCfgVo {
	private String cfgCode;
	private String cfgDescription;
	private String corpCode;
	private String statementCode;
	private String statementName;
	private String cfgContent;
	private Short cfgType;
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT-8")
	private Date createTime;
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT-8")
	private Date updateTime;
}