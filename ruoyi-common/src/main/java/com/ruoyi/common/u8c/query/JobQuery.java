package com.ruoyi.common.u8c.query;

import com.ruoyi.common.utils.StringUtils;

/**
 * 项目查询实体
 */
public class JobQuery {
    private String jobname; // 项目名称，精确匹配
    private String name; // 项目名称，模糊匹配
    private String jobtypecode; // 项目类型，项目的类型为06

    public String getJobname() {
        return jobname;
    }

    public void setJobname(String jobname) {
        this.jobname = jobname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getJobtypecode() {
        return jobtypecode;
    }

    public void setJobtypecode(String jobtypecode) {
        this.jobtypecode = jobtypecode;
    }
}
