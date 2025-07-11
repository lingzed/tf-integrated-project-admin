package com.ruoyi.common.u8c.warpper;

import com.ruoyi.common.constant.PeriodConstants;
import com.ruoyi.common.u8c.SubjBalance;

import java.util.List;

public interface ByPeriodWrapper<T> {
    String CURR_PERIOD = "本期";    // 本期
    String PRE_PERIOD = "上期";    // 上期
    String PERIOD_12 = "期间12";    // 期间12
    String PERIOD_1 = "期间1";    // 期间1
    String CURR_YEAR = "本年";    // 本年
    String PRE_YEAR = "上年";    // 上年
    String PRE_YEAR_SAME_PERIOD = "上年同期";  // 上年同期
    String PRE_YEAR_PER_PERIOD = "上年上期";           // 上年上期
    String FIRST_PERIOD_TO_CURR = "期初到本期";
    String FIRST_PERIOD_TO_PRE = "期初到上期";

    /**
     * 设置本期的财务数据列表
     * @param list
     */
    void setCurrPeriod(List<T> list);

    /**
     * 获取本期的财务数据列表
     */
    List<T> getCurrPeriod();

    /**
     * 设置上期的财务数据列表
     * @param list
     */
    void setPrePeriod(List<T> list);

    /**
     * 获取上期的财务数据列表
     * @return
     */
    List<T> getPrePeriod();

    /**
     * 设置期间12的财务数据列表
     * @param list
     */
    void setPeriod12(List<T> list);

    /**
     * 获取期间12的财务数据列表
     * @return
     */
    List<T> getPeriod12();

    /**
     * 设置上年同期的财务数据列表
     * @param list
     * @return
     */
    void setPreYearSamePeriod(List<T> list);

    /**
     * 获取上年同期的财务数据列表
     * @return
     */
    List<T> getPreYearSamePeriod();
}
