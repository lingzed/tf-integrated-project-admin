package com.ruoyi.common.core.domain.entity;

/**
 * 公司表实体
 */
public class Corporation {
    private Integer id;     // id
    private String corpCode;    // 公司编码
    private String pCorpCode;   // 公司上级编码
    private String corpName;    // 公司名称

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCorpCode() {
        return corpCode;
    }

    public void setCorpCode(String corpCode) {
        this.corpCode = corpCode;
    }

    public String getpCorpCode() {
        return pCorpCode;
    }

    public void setpCorpCode(String pCorpCode) {
        this.pCorpCode = pCorpCode;
    }

    public String getCorpName() {
        return corpName;
    }

    public void setCorpName(String corpName) {
        this.corpName = corpName;
    }
}
