package com.ruoyi.common.u8c.query;

import java.util.Set;

/**
 * 科目查询实体
 */
public class SubjectQuery {
    private String pk_subjscheme;   // 主账簿
    private String page_now;        // 当前页
    private String page_size;       // 每页条目
    private Set<String> subjcode;        // 科目编码
    private String unitcode;        // 公司编码

    public String getPk_subjscheme() {
        return pk_subjscheme;
    }

    public void setPk_subjscheme(String pk_subjscheme) {
        this.pk_subjscheme = pk_subjscheme == null ? "0003" : pk_subjscheme;
    }

    public String getPage_now() {
        return page_now;
    }

    public void setPage_now(String page_now) {
        this.page_now = page_now;
    }

    public String getPage_size() {
        return page_size;
    }

    public void setPage_size(String page_size) {
        this.page_size = page_size;
    }

    public Set<String> getSubjcode() {
        return subjcode;
    }

    public void setSubjcode(Set<String> subjcode) {
        this.subjcode = subjcode;
    }

    public String getUnitcode() {
        return unitcode;
    }

    public void setUnitcode(String unitcode) {
        this.unitcode = unitcode == null ? "0001" : unitcode;
    }
}
