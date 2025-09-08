package com.ruoyi.common.u8c;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 凭证实体
 */
public class Voucher {
    private String no;  // 凭证号
    private String glorgbook_code;  // 账簿编码
    private String pk_voucher;  // 凭证表中这条凭证的主键
    private String explanation; // 凭证的摘要
    private BigDecimal totaldebit;  // 借方总发生数
    private BigDecimal totalcredit;  // 贷方总发生数
    private List<Detail> detail;    // 凭证的分录列表
    private String year;    // 凭证的分录列表
    private String period;    // 凭证的分录列表
    private String prepareddate;  // 制单日期

    public String getPk_voucher() {
        return pk_voucher;
    }

    public void setPk_voucher(String pk_voucher) {
        this.pk_voucher = pk_voucher;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public BigDecimal getTotaldebit() {
        return totaldebit;
    }

    public void setTotaldebit(BigDecimal totaldebit) {
        this.totaldebit = totaldebit;
    }

    public BigDecimal getTotalcredit() {
        return totalcredit;
    }

    public void setTotalcredit(BigDecimal totalcredit) {
        this.totalcredit = totalcredit;
    }

    public List<Detail> getDetail() {
        return detail;
    }

    public void setDetail(List<Detail> detail) {
        this.detail = detail;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getPeriod() {
        if (year == null || period == null) return "";
        return year + "-" + period;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getGlorgbook_code() {
        return glorgbook_code;
    }

    public void setGlorgbook_code(String glorgbook_code) {
        this.glorgbook_code = glorgbook_code;
    }

    public String getPrepareddate() {
        return prepareddate;
    }

    public void setPrepareddate(String prepareddate) {
        this.prepareddate = prepareddate;
    }
}
