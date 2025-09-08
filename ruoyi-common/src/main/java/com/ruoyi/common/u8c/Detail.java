package com.ruoyi.common.u8c;

import java.math.BigDecimal;
import java.util.List;

/**
 * 凭证分录
 */
public class Detail {
    private String detailindex;   // 分录号
    private String pk_detail;   // 分录的主键
    private String accsubj_code;    // 科目编码
    private String accsubj_name;    // 科目名称
    private String explanation;     // 摘要
    private BigDecimal localdebitamount;    // 本币借方余额
    private BigDecimal localcreditamount;    // 本币贷方余额
    private List<AssVo> ass;                // 辅助核算
    private String period;

    public String getPk_detail() {
        return pk_detail;
    }

    public void setPk_detail(String pk_detail) {
        this.pk_detail = pk_detail;
    }

    public String getAccsubj_code() {
        return accsubj_code;
    }

    public void setAccsubj_code(String accsubj_code) {
        this.accsubj_code = accsubj_code;
    }

    public String getAccsubj_name() {
        return accsubj_name;
    }

    public void setAccsubj_name(String accsubj_name) {
        this.accsubj_name = accsubj_name;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public BigDecimal getLocaldebitamount() {
        return localdebitamount;
    }

    public void setLocaldebitamount(BigDecimal localdebitamount) {
        this.localdebitamount = localdebitamount;
    }

    public BigDecimal getLocalcreditamount() {
        return localcreditamount;
    }

    public void setLocalcreditamount(BigDecimal localcreditamount) {
        this.localcreditamount = localcreditamount;
    }

    public List<AssVo> getAss() {
        return ass;
    }

    public void setAss(List<AssVo> ass) {
        this.ass = ass;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getDetailindex() {
        return detailindex;
    }

    public void setDetailindex(String detailindex) {
        this.detailindex = detailindex;
    }
}
