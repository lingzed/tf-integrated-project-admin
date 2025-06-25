package com.ruoyi.system.domain.statementcfg;

import java.util.List;

/**
 * 期间写入的相关配置
 */
public class PeriodWriteCfg {
    private Boolean isWrite;                    // 是否写入期间
    private String dateFormat;                     // 日期格式，yyyy、yyyy-MM、yyyy-MM-dd
    private Integer writeType;                  // 写入类型，0: 覆盖写入, 1: 格式化
    private List<Integer> writeArea;            // 写入区域
    private Boolean isChinese;                  // 是否中文日期格式

    public Boolean getWrite() {
        return isWrite;
    }

    public void setWrite(Boolean write) {
        isWrite = write;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public Integer getWriteType() {
        return writeType;
    }

    public void setWriteType(Integer writeType) {
        this.writeType = writeType;
    }

    public List<Integer> getWriteArea() {
        return writeArea;
    }

    public void setWriteArea(List<Integer> writeArea) {
        this.writeArea = writeArea;
    }

    public Boolean getChinese() {
        return isChinese;
    }

    public void setChinese(Boolean chinese) {
        isChinese = chinese;
    }
}
