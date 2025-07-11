package com.ruoyi.system.domain.statement.dto;

import com.ruoyi.common.constant.MsgConstants;
import com.ruoyi.common.utils.StmtRelatedUtil;
import com.ruoyi.common.validated.group.Add;
import com.ruoyi.common.validated.group.Edit;
import com.ruoyi.common.validated.json.JsonCont;

import javax.validation.constraints.*;

public class StatementCfgDto {
    private Integer cfgId;  // 配置项的id
    //    private String cfgCode; // 配置的编码, 适配redis的key, 格式为 stmt_cfg:报表编码:公司编码:配置类型[:其他信息]
    private String cfgDescription;  // 配置项的描述
    private String corpCode;    // 配置项所属的公司编码
    private String statementCode;   // 配置项对应的报表的编码
    private String statementName;   // 配置项对应的报表的名称
    private String cfgContent;   // 配置项的json内容
    private Short contentJsonType;  // 配置内容的json类型
    private Short cfgType;    // 配置项的类型, 0: 行列头索引配置, 1: 工作表写入配置, 2: 查询配置, 3: 行列头映射数据集配置
    private String other;   // key中的[:其他信息]
    private Integer lockVersion;    // 乐观锁

    @NotNull(message = MsgConstants.ID_REQUIRED, groups = {Edit.class})
    @Min(value = 1, message = MsgConstants.ERROR_ID, groups = {Edit.class})
    public Integer getCfgId() {
        return cfgId;
    }

    public void setCfgId(Integer cfgId) {
        this.cfgId = cfgId;
    }

    public String getCfgCode() {
        return StmtRelatedUtil.getCfgCode(statementCode, cfgType, corpCode, other);
    }

    public String getCfgDescription() {
        return cfgDescription;
    }

    public void setCfgDescription(String cfgDescription) {
        this.cfgDescription = cfgDescription;
    }

    @NotNull(message = MsgConstants.CORP_CODE_REQUIRED, groups = {Add.class})
    @NotBlank(message = MsgConstants.CORP_CODE_REQUIRED, groups = {Add.class})
    public String getCorpCode() {
        return corpCode;
    }

    public void setCorpCode(String corpCode) {
        this.corpCode = corpCode;
    }

    @NotNull(message = MsgConstants.STMT_CODE_REQUIRED, groups = {Add.class})
    @NotBlank(message = MsgConstants.STMT_CODE_REQUIRED, groups = {Add.class})
    @Pattern(regexp = "^[^\\u4e00-\\u9fa5]*$", message = MsgConstants.STMT_CODE_NOT_CHINESE, groups = {Add.class})
    public String getStatementCode() {
        return statementCode;
    }

    public void setStatementCode(String statementCode) {
        this.statementCode = statementCode;
    }

    @NotNull(message = MsgConstants.STMT_NAME_REQUIRED, groups = {Add.class})
    @NotBlank(message = MsgConstants.STMT_NAME_REQUIRED, groups = {Add.class})
    public String getStatementName() {
        return statementName;
    }

    public void setStatementName(String statementName) {
        this.statementName = statementName;
    }

    @NotNull(message = MsgConstants.CFG_CONT_REQUIRED, groups = {Add.class, Edit.class})
    @NotBlank(message = MsgConstants.CFG_CONT_REQUIRED, groups = {Add.class, Edit.class})
    @JsonCont(message = MsgConstants.CFG_CONT_NOT_LEGAL_FORMAT, groups = {Add.class, Edit.class})
    public String getCfgContent() {
        return cfgContent;
    }

    public void setCfgContent(String cfgContent) {
        this.cfgContent = cfgContent;
    }

    @NotNull(message = MsgConstants.CFG_CONT_TYPE_REQUIRED, groups = {Add.class, Edit.class})
    @Min(value = 0, message = MsgConstants.UNKNOWN_J_TYPE, groups = {Add.class, Edit.class})
    @Max(value = 5, message = MsgConstants.UNKNOWN_J_TYPE, groups = {Add.class, Edit.class})
    public Short getContentJsonType() {
        return contentJsonType;
    }

    public void setContentJsonType(Short contentJsonType) {
        this.contentJsonType = contentJsonType;
    }

    @NotNull(message = MsgConstants.CFG_TYPE_REQUIRED, groups = {Add.class})
    @Min(value = 0, message = MsgConstants.UNKNOWN_STMT_CFG_TYPE_V1, groups = {Add.class})
    @Max(value = 3, message = MsgConstants.UNKNOWN_STMT_CFG_TYPE_V1, groups = {Add.class})
    public Short getCfgType() {
        return cfgType;
    }

    public void setCfgType(Short cfgType) {
        this.cfgType = cfgType;
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }

    public Integer getLockVersion() {
        return lockVersion;
    }

    public void setLockVersion(Integer lockVersion) {
        this.lockVersion = lockVersion;
    }
}
