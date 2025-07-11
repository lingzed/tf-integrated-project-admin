package com.ruoyi.common.u8c.subj;

/**
 * 科目接口返回的最内层的科目对象
 */
public class AccSubjParentVO {
    private String balanorient; // 科目方向, 1: 借, 2: 贷
    private String pk_glorgbook;    // 会计主体账簿
    private String subjcode;    // 科目编码
    private String subjname;    // 科目名称

    public String getBalanorient() {
        return balanorient;
    }

    public void setBalanorient(String balanorient) {
        this.balanorient = balanorient;
    }

    public String getPk_glorgbook() {
        return pk_glorgbook;
    }

    public void setPk_glorgbook(String pk_glorgbook) {
        this.pk_glorgbook = pk_glorgbook;
    }

    public String getSubjcode() {
        return subjcode;
    }

    public void setSubjcode(String subjcode) {
        this.subjcode = subjcode;
    }

    public String getSubjname() {
        return subjname;
    }

    public void setSubjname(String subjname) {
        this.subjname = subjname;
    }
}
