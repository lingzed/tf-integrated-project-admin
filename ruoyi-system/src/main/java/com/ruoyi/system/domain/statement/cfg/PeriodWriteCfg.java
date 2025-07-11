package com.ruoyi.system.domain.statement.cfg;

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

    /**
     * javabean中的setter，如果是boolean类型以isXxx开头的字段，生成的setter并非isXxx()，而是setXxx()
     * 一般我们反序列一个json字符串时，通常都是保证json字段与javabean字段一致
     * 而FastJSON反序列化靠字段名来推断setter
     * 所以它看到json中的有isXxx这样的字段，推出的setter为setIsXxx()
     * 因此如果不作处理，那么isXxx这样的字段就会反序列化失败
     * 因此还得定义一个setIsXXX()以方便FastJSON反序列化
     * 或者不要用isXxx这样的命名，而直接xxx
     * @param write
     */
    public void setIsWrite(Boolean write) {
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

    public void setIsChinese(Boolean isChinese){
        this.isChinese = isChinese;
    }
}
