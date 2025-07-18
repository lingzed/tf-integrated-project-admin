package com.ruoyi.system.domain.statement.query;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;
import java.util.Objects;

/**
 * 基础查询Dto
 */
public class BaseQuery {
    @JsonIgnore
    private Integer pageNum;           // 当前页
    @JsonIgnore
    private Integer pageSize;       // 当前页展示条目
    @JsonIgnore
    private Date startDate;         // 开始时间
    @JsonIgnore
    private Date endDate;           // 结束时间
    @JsonIgnore
    private String orderBy;         // 排序sql


    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    /*分页起始索引*/
    public Integer getIndex() {
        if (Objects.isNull(pageNum) || Objects.isNull(pageSize)) {
            return null;
        }
        return (pageNum - 1) * pageSize;
    }

    /*每页条目*/
    public Integer getSize() {
        return getPageSize();
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }
}
