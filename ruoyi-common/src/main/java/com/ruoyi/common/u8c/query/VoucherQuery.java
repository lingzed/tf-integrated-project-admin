package com.ruoyi.common.u8c.query;

import com.alibaba.fastjson2.annotation.JSONField;

import java.util.List;
import java.util.Set;

/**
 * 凭证查询实体
 */
public class VoucherQuery {
    @JSONField(ordinal = 1)
    private String pk_glorgbook;
    @JSONField(ordinal = 2)
    private String prepareddate_from;
    @JSONField(ordinal = 3)
    private String prepareddate_to;
    @JSONField(ordinal = 4)
    private Integer page_now;
    @JSONField(ordinal = 5)
    private Integer page_size;
    @JSONField(ordinal = 6)
    private Set<String> subjcode;

    public String getPk_glorgbook() {
        return pk_glorgbook;
    }

    public void setPk_glorgbook(String pk_glorgbook) {
        this.pk_glorgbook = pk_glorgbook;
    }

    public String getPrepareddate_from() {
        return prepareddate_from;
    }

    public void setPrepareddate_from(String prepareddate_from) {
        this.prepareddate_from = prepareddate_from;
    }

    public String getPrepareddate_to() {
        return prepareddate_to;
    }

    public void setPrepareddate_to(String prepareddate_to) {
        this.prepareddate_to = prepareddate_to;
    }

    public Integer getPage_now() {
        return page_now;
    }

    public void setPage_now(Integer page_now) {
        this.page_now = page_now;
    }

    public Integer getPage_size() {
        return page_size;
    }

    public void setPage_size(Integer page_size) {
        this.page_size = page_size;
    }

    public Set<String> getSubjcode() {
        return subjcode;
    }

    public void setSubjcode(Set<String> subjcode) {
        this.subjcode = subjcode;
    }
}
