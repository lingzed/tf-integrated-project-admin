package com.ruoyi.common.u8c;

/**
 * 辅助核算项目实体
 */
public class AuxAcctProject {
    private String asscode;     // 辅助核算项编码
    private String assname;     // 辅助核算项名称
    private String pk_assvalue; // 辅助核算项主键
    private String asstypecode; // 辅助核算项类型
    private String asstypename; // 辅助核算项类型名称
    private String asstypeid;   // 辅助核算项类型主键

    public String getAsscode() {
        return asscode;
    }

    public void setAsscode(String asscode) {
        this.asscode = asscode;
    }

    public String getAssname() {
        return assname;
    }

    public void setAssname(String assname) {
        this.assname = assname;
    }

    public String getPk_assvalue() {
        return pk_assvalue;
    }

    public void setPk_assvalue(String pk_assvalue) {
        this.pk_assvalue = pk_assvalue;
    }

    public String getAsstypecode() {
        return asstypecode;
    }

    public void setAsstypecode(String asstypecode) {
        this.asstypecode = asstypecode;
    }

    public String getAsstypename() {
        return asstypename;
    }

    public void setAsstypename(String asstypename) {
        this.asstypename = asstypename;
    }

    public String getAsstypeid() {
        return asstypeid;
    }

    public void setAsstypeid(String asstypeid) {
        this.asstypeid = asstypeid;
    }
}
