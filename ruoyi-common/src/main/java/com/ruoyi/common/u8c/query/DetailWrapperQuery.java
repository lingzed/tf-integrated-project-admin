package com.ruoyi.common.u8c.query;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 客户对账查询实体
 */
public class DetailWrapperQuery {
    private List<String> glBookCodes;  // 主体账簿
    private String startDate;   // 开始日期
    private String endDate;   // 结束日期
    private Set<String> subjCodes;  // 科目编码
    private String ctrName; // 客户名称
    private String jobPjtName; // 项目名称

    public List<String> getGlBookCodes() {
        return glBookCodes;
    }

    public void setGlBookCodes(List<String> glBookCodes) {
        this.glBookCodes = glBookCodes.stream().map(code -> code + "-0003").collect(Collectors.toList());
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public Set<String> getSubjCodes() {
        return subjCodes;
    }

    public void setSubjCodes(Set<String> subjCodes) {
        this.subjCodes = subjCodes;
    }

    public String getCtrName() {
        return ctrName;
    }

    public void setCtrName(String ctrName) {
        this.ctrName = ctrName;
    }

    public String getJobPjtName() {
        return jobPjtName;
    }

    public void setJobPjtName(String jobPjtName) {
        this.jobPjtName = jobPjtName;
    }
}
