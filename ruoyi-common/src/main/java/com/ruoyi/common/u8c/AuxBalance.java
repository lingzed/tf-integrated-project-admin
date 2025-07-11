package com.ruoyi.common.u8c;

import java.math.BigDecimal;
import java.util.List;

/**
 * 辅助余额实体
 */
public class AuxBalance implements Balance {
    private String pk_corp; // 公司主键
    private String pk_corp_code;    // 公司编码
    private String pk_corp_name;    // 公司名称
    private String pk_glorgbook;    // 账簿主键
    private String glorgbook_code;  // 账簿编码
    private String glorgbook_name;  // 账簿名称
    private BigDecimal initCreditQuant;     // 期初贷方(数量)
    private BigDecimal initCreditAmount;    // 期初贷方余额(原币)
    private BigDecimal initCreditAuxAmount; // 期初贷方余额(辅币)
    private BigDecimal initCreditLocAmount; // 期初贷方余额(本币)
    private BigDecimal initDebitQuant;      // 期初借方(数量)
    private BigDecimal initDebitAmount;     // 期初借方余额(原币)
    private BigDecimal initDebitAuxAmount;  // 期初借方余额(辅币)
    private BigDecimal initDebitLocAmount;  // 期初借方余额(本币)
    private BigDecimal debitQuant;           // 本期借方(数量)
    private BigDecimal debitAmount;    // 本期借方发生额(原币)
    private BigDecimal debitAuxAmount; // 本期借方发生额(辅币)
    private BigDecimal debitLocAmount; // 本期借方发生额(本币)
    private BigDecimal creditQuant;           // 本期贷方(数量)
    private BigDecimal creditAmount;    // 本期贷方发生额(原币)
    private BigDecimal creditAuxAmount; // 本期贷方发生额(辅币)
    private BigDecimal creditLocAmount; // 本期贷方发生额(本币)
    private BigDecimal debitAccumQuant;           // 借方累计(数量)
    private BigDecimal debitAccumAmount;    // 借方累计(原币)
    private BigDecimal debitAccumAuxAmount; // 借方累计(辅币)
    private BigDecimal debitAccumLocAmount; // 借方累计(本币)
    private BigDecimal creditAccumQuant;              // 贷方累计(数量)
    private BigDecimal creditAccumAmount;       // 贷方累计(原币)
    private BigDecimal creditAccumAuxAmount;    // 贷方累计(辅币)
    private BigDecimal creditAccumLocAmount;    // 贷方累计(本币)
    private BigDecimal endDebitQuant;             // 期末借方余额(数量)
    private BigDecimal endDebitAmount;      // 期末借方余额(原币)
    private BigDecimal endDebitAuxAmount;   // 期末借方余额(辅币)
    private BigDecimal endDebitLocAmount;   // 期末借方余额(本币)
    private BigDecimal endCreditQuant;            // 期末贷方余额(数量)
    private BigDecimal endCreditAmount;     // 期末贷方余额(原币)
    private BigDecimal endCreditAuxAmount;  // 期末贷方余额(辅币)
    private BigDecimal endCreditLocAmount;  // 期末贷方余额(本币)
    private String pk_accsubj;  // 科目主键
    private String pk_accsubj_code; // 科目编码
    private String pk_accsubj_name; // 科目编名称
    private List<AuxAcctProject> glqueryassvo;
    private String endOrient;   // 期末方向
    private String initOrient;  // 期初方向
    private String accyear;  // 期间年份
    private String periodMonth;  // 查询的期间月，非接口返回的标准字段，主要用于标识每条数据的月份
    private String period;  // 查询的期间

    public String getPk_corp() {
        return pk_corp;
    }

    public void setPk_corp(String pk_corp) {
        this.pk_corp = pk_corp;
    }

    public String getPk_corp_code() {
        return pk_corp_code;
    }

    public void setPk_corp_code(String pk_corp_code) {
        this.pk_corp_code = pk_corp_code;
    }

    public String getPk_corp_name() {
        return pk_corp_name;
    }

    public void setPk_corp_name(String pk_corp_name) {
        this.pk_corp_name = pk_corp_name;
    }

    public String getPk_glorgbook() {
        return pk_glorgbook;
    }

    public void setPk_glorgbook(String pk_glorgbook) {
        this.pk_glorgbook = pk_glorgbook;
    }

    public String getGlorgbook_code() {
        return glorgbook_code;
    }

    public void setGlorgbook_code(String glorgbook_code) {
        this.glorgbook_code = glorgbook_code;
    }

    public String getGlorgbook_name() {
        return glorgbook_name;
    }

    public void setGlorgbook_name(String glorgbook_name) {
        this.glorgbook_name = glorgbook_name;
    }

    public BigDecimal getInitCreditQuant() {
        return initCreditQuant;
    }

    public void setInitCreditQuant(BigDecimal initCreditQuant) {
        this.initCreditQuant = initCreditQuant;
    }

    public BigDecimal getInitCreditAmount() {
        return initCreditAmount;
    }

    public void setInitCreditAmount(BigDecimal initCreditAmount) {
        this.initCreditAmount = initCreditAmount;
    }

    public BigDecimal getInitCreditAuxAmount() {
        return initCreditAuxAmount;
    }

    public void setInitCreditAuxAmount(BigDecimal initCreditAuxAmount) {
        this.initCreditAuxAmount = initCreditAuxAmount;
    }

    public BigDecimal getInitCreditLocAmount() {
        return initCreditLocAmount;
    }

    public void setInitCreditLocAmount(BigDecimal initCreditLocAmount) {
        this.initCreditLocAmount = initCreditLocAmount;
    }

    public BigDecimal getInitDebitQuant() {
        return initDebitQuant;
    }

    public void setInitDebitQuant(BigDecimal initDebitQuant) {
        this.initDebitQuant = initDebitQuant;
    }

    public BigDecimal getInitDebitAmount() {
        return initDebitAmount;
    }

    public void setInitDebitAmount(BigDecimal initDebitAmount) {
        this.initDebitAmount = initDebitAmount;
    }

    public BigDecimal getInitDebitAuxAmount() {
        return initDebitAuxAmount;
    }

    public void setInitDebitAuxAmount(BigDecimal initDebitAuxAmount) {
        this.initDebitAuxAmount = initDebitAuxAmount;
    }

    public BigDecimal getInitDebitLocAmount() {
        return initDebitLocAmount;
    }

    public void setInitDebitLocAmount(BigDecimal initDebitLocAmount) {
        this.initDebitLocAmount = initDebitLocAmount;
    }

    public BigDecimal getDebitQuant() {
        return debitQuant;
    }

    public void setDebitQuant(BigDecimal debitQuant) {
        this.debitQuant = debitQuant;
    }

    public BigDecimal getDebitAmount() {
        return debitAmount;
    }

    public void setDebitAmount(BigDecimal debitAmount) {
        this.debitAmount = debitAmount;
    }

    public BigDecimal getDebitAuxAmount() {
        return debitAuxAmount;
    }

    public void setDebitAuxAmount(BigDecimal debitAuxAmount) {
        this.debitAuxAmount = debitAuxAmount;
    }

    public BigDecimal getDebitLocAmount() {
        return debitLocAmount;
    }

    public void setDebitLocAmount(BigDecimal debitLocAmount) {
        this.debitLocAmount = debitLocAmount;
    }

    public BigDecimal getCreditQuant() {
        return creditQuant;
    }

    public void setCreditQuant(BigDecimal creditQuant) {
        this.creditQuant = creditQuant;
    }

    public BigDecimal getCreditAmount() {
        return creditAmount;
    }

    public void setCreditAmount(BigDecimal creditAmount) {
        this.creditAmount = creditAmount;
    }

    public BigDecimal getCreditAuxAmount() {
        return creditAuxAmount;
    }

    public void setCreditAuxAmount(BigDecimal creditAuxAmount) {
        this.creditAuxAmount = creditAuxAmount;
    }

    public BigDecimal getCreditLocAmount() {
        return creditLocAmount;
    }

    public void setCreditLocAmount(BigDecimal creditLocAmount) {
        this.creditLocAmount = creditLocAmount;
    }

    public BigDecimal getDebitAccumQuant() {
        return debitAccumQuant;
    }

    public void setDebitAccumQuant(BigDecimal debitAccumQuant) {
        this.debitAccumQuant = debitAccumQuant;
    }

    public BigDecimal getDebitAccumAmount() {
        return debitAccumAmount;
    }

    public void setDebitAccumAmount(BigDecimal debitAccumAmount) {
        this.debitAccumAmount = debitAccumAmount;
    }

    public BigDecimal getDebitAccumAuxAmount() {
        return debitAccumAuxAmount;
    }

    public void setDebitAccumAuxAmount(BigDecimal debitAccumAuxAmount) {
        this.debitAccumAuxAmount = debitAccumAuxAmount;
    }

    public BigDecimal getDebitAccumLocAmount() {
        return debitAccumLocAmount;
    }

    public void setDebitAccumLocAmount(BigDecimal debitAccumLocAmount) {
        this.debitAccumLocAmount = debitAccumLocAmount;
    }

    public BigDecimal getCreditAccumQuant() {
        return creditAccumQuant;
    }

    public void setCreditAccumQuant(BigDecimal creditAccumQuant) {
        this.creditAccumQuant = creditAccumQuant;
    }

    public BigDecimal getCreditAccumAmount() {
        return creditAccumAmount;
    }

    public void setCreditAccumAmount(BigDecimal creditAccumAmount) {
        this.creditAccumAmount = creditAccumAmount;
    }

    public BigDecimal getCreditAccumAuxAmount() {
        return creditAccumAuxAmount;
    }

    public void setCreditAccumAuxAmount(BigDecimal creditAccumAuxAmount) {
        this.creditAccumAuxAmount = creditAccumAuxAmount;
    }

    public BigDecimal getCreditAccumLocAmount() {
        return creditAccumLocAmount;
    }

    public void setCreditAccumLocAmount(BigDecimal creditAccumLocAmount) {
        this.creditAccumLocAmount = creditAccumLocAmount;
    }

    public BigDecimal getEndDebitQuant() {
        return endDebitQuant;
    }

    public void setEndDebitQuant(BigDecimal endDebitQuant) {
        this.endDebitQuant = endDebitQuant;
    }

    public BigDecimal getEndDebitAmount() {
        return endDebitAmount;
    }

    public void setEndDebitAmount(BigDecimal endDebitAmount) {
        this.endDebitAmount = endDebitAmount;
    }

    public BigDecimal getEndDebitAuxAmount() {
        return endDebitAuxAmount;
    }

    public void setEndDebitAuxAmount(BigDecimal endDebitAuxAmount) {
        this.endDebitAuxAmount = endDebitAuxAmount;
    }

    public BigDecimal getEndDebitLocAmount() {
        return endDebitLocAmount;
    }

    public void setEndDebitLocAmount(BigDecimal endDebitLocAmount) {
        this.endDebitLocAmount = endDebitLocAmount;
    }

    public BigDecimal getEndCreditQuant() {
        return endCreditQuant;
    }

    public void setEndCreditQuant(BigDecimal endCreditQuant) {
        this.endCreditQuant = endCreditQuant;
    }

    public BigDecimal getEndCreditAmount() {
        return endCreditAmount;
    }

    public void setEndCreditAmount(BigDecimal endCreditAmount) {
        this.endCreditAmount = endCreditAmount;
    }

    public BigDecimal getEndCreditAuxAmount() {
        return endCreditAuxAmount;
    }

    public void setEndCreditAuxAmount(BigDecimal endCreditAuxAmount) {
        this.endCreditAuxAmount = endCreditAuxAmount;
    }

    public BigDecimal getEndCreditLocAmount() {
        return endCreditLocAmount;
    }

    public void setEndCreditLocAmount(BigDecimal endCreditLocAmount) {
        this.endCreditLocAmount = endCreditLocAmount;
    }

    public String getPk_accsubj() {
        return pk_accsubj;
    }

    public void setPk_accsubj(String pk_accsubj) {
        this.pk_accsubj = pk_accsubj;
    }

    public String getPk_accsubj_code() {
        return pk_accsubj_code;
    }

    public void setPk_accsubj_code(String pk_accsubj_code) {
        this.pk_accsubj_code = pk_accsubj_code;
    }

    public String getPk_accsubj_name() {
        return pk_accsubj_name;
    }

    public void setPk_accsubj_name(String pk_accsubj_name) {
        this.pk_accsubj_name = pk_accsubj_name;
    }

    public List<AuxAcctProject> getGlqueryassvo() {
        return glqueryassvo;
    }

    public void setGlqueryassvo(List<AuxAcctProject> glqueryassvo) {
        this.glqueryassvo = glqueryassvo;
    }

    public String getEndOrient() {
        return endOrient;
    }

    public void setEndOrient(String endOrient) {
        this.endOrient = endOrient;
    }

    public String getInitOrient() {
        return initOrient;
    }

    public void setInitOrient(String initOrient) {
        this.initOrient = initOrient;
    }

    public String getAccyear() {
        return accyear;
    }

    public void setAccyear(String accyear) {
        this.accyear = accyear;
    }

    public String getPeriodMonth() {
        return periodMonth;
    }

    public void setPeriodMonth(String periodMonth) {
        this.periodMonth = periodMonth;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }
}
