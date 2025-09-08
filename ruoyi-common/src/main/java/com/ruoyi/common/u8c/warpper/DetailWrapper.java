package com.ruoyi.common.u8c.warpper;

import com.ruoyi.common.u8c.Detail;

/**
 * 凭证分录包装实体，映射客户对账查询的中间数据
 */
public class DetailWrapper extends Detail {
    private String date;
    private String gBookCode;   // 账套号
    private String noAndIndex;  // 凭证号-分录号
    private String no;  // 凭证号
    private String ctrCode; // 客户编码
    private String ctrName; // 客户名称
    private String jobPjtCode;  // 项目编码
    private String jobPjtName;  // 项目名称

    public void setNo(String no) {
        this.no = no;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getgBookCode() {
        return gBookCode;
    }

    public void setgBookCode(String gBookCode) {
        this.gBookCode = gBookCode;
    }

    public String getNoAndIndex() {
        return no + "-" + getDetailindex();
    }

    public void setNoAndIndex(String noAndIndex) {
        this.noAndIndex = noAndIndex;
    }

    public String getCtrCode() {
        return ctrCode;
    }

    public void setCtrCode(String ctrCode) {
        this.ctrCode = ctrCode;
    }

    public String getCtrName() {
        return ctrName;
    }

    public void setCtrName(String ctrName) {
        this.ctrName = ctrName;
    }

    public String getJobPjtCode() {
        return jobPjtCode;
    }

    public void setJobPjtCode(String jobPjtCode) {
        this.jobPjtCode = jobPjtCode;
    }

    public String getJobPjtName() {
        return jobPjtName;
    }

    public void setJobPjtName(String jobPjtName) {
        this.jobPjtName = jobPjtName;
    }
}
