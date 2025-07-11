package com.ruoyi.common.u8c.query;

/**
 * 科目余额查询实体
 */
public class SubjBalanceQuery {
    private String pk_corp; // 公司编码
    private String pk_glorgbook;    // 账簿编码
    private String startPeriod; // 开始期间
    private String endPeriod;   // 结束期间
    private String accsubjcodeFrom;   // 科目从
    private String accsubjcodeTo;   // 科目至

    public String getPk_corp() {
        return pk_corp;
    }

    public void setPk_corp(String pk_corp) {
        this.pk_corp = pk_corp;
    }

    public String getPk_glorgbook() {
        return pk_glorgbook;
    }

    public void setPk_glorgbook(String pk_glorgbook) {
        this.pk_glorgbook = pk_glorgbook;
    }

    public String getStartPeriod() {
        return startPeriod;
    }

    public void setStartPeriod(String startPeriod) {
        this.startPeriod = startPeriod;
    }

    public String getEndPeriod() {
        return endPeriod;
    }

    public void setEndPeriod(String endPeriod) {
        this.endPeriod = endPeriod;
    }

    public String getAccsubjcodeFrom() {
        return accsubjcodeFrom;
    }

    public void setAccsubjcodeFrom(String accsubjcodeFrom) {
        this.accsubjcodeFrom = accsubjcodeFrom;
    }

    public String getAccsubjcodeTo() {
        return accsubjcodeTo;
    }

    public void setAccsubjcodeTo(String accsubjcodeTo) {
        this.accsubjcodeTo = accsubjcodeTo;
    }
}
