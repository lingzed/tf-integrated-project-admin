package com.ruoyi.common.u8c.query;

import com.ruoyi.common.u8c.AssVo;

import java.util.List;
import java.util.Set;

/**
 * 辅助余额查询实体
 */
public class AuxBalanceQuery {
    private String pk_corp;         // 公司编码
    private String pk_glorgbook;    // 主体账簿
    private String startPeriod;     // 开始期间
    private String endPeriod;       // 结束期间
    private Set<String> pk_accsubj;      // 会计科目
    private String includeErrorVoucher; // 是否包含错误凭证
    private String includeUntallyVoucher;   // 是否包含未记账凭证
    private String includeInstVoucher;  // 是否包含实时凭证
    private List<AssVo> assvo;  // 辅助核算

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

    public Set<String> getPk_accsubj() {
        return pk_accsubj;
    }

    public void setPk_accsubj(Set<String> pk_accsubj) {
        this.pk_accsubj = pk_accsubj;
    }

    public String getIncludeErrorVoucher() {
        return includeErrorVoucher;
    }

    public void setIncludeErrorVoucher(String includeErrorVoucher) {
        this.includeErrorVoucher = includeErrorVoucher;
    }

    public String getIncludeUntallyVoucher() {
        return includeUntallyVoucher;
    }

    public void setIncludeUntallyVoucher(String includeUntallyVoucher) {
        this.includeUntallyVoucher = includeUntallyVoucher;
    }

    public String getIncludeInstVoucher() {
        return includeInstVoucher;
    }

    public void setIncludeInstVoucher(String includeInstVoucher) {
        this.includeInstVoucher = includeInstVoucher;
    }

    public List<AssVo> getAssvo() {
        return assvo;
    }

    public void setAssvo(List<AssVo> assvo) {
        this.assvo = assvo;
    }
}
