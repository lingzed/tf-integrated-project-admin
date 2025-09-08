package com.ruoyi.common.u8c.warpper;

import java.util.List;

/**
 * 通过期间包装
 * @param <T>
 */
public interface ByPeriodWrapper<T> {
    String CURR_PERIOD = "本期";    // 本期
    String PRE_PERIOD = "上期";    // 上期
    String PERIOD_12 = "期间12";    // 期间12
    String PERIOD_1 = "期间1";    // 期间1
    String CURR_YEAR = "本年";    // 本年
    String PRE_YEAR = "上年";    // 上年
    String P_PRE_YEAR = "上上年";
    String PRE_YEAR_SAME_PERIOD = "上年同期";  // 上年同期
    String PRE_YEAR_PER_PERIOD = "上年上期";           // 上年上期
    String FIRST_PERIOD_TO_CURR = "期初到本期";
    String PRE_YEAR_FIRST_TO_CURR = "上年期初到上年同期";
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
     * 设置期间1的财务数据列表
     * @param list
     */
    void setPeriod1(List<T> list);

    /**
     * 获取期间1的财务数据列表
     * @return
     */
    List<T> getPeriod1();

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

    /**
     * 设置期初到当期财务数据列表
     * @param list
     */
    void setFirstPeriodToCurr(List<T> list);

    /**
     * 获取期初到当期财务数据列表
     * @return
     */
    List<T> getFirstPeriodToCurr();

    /**
     * 设置上年期初到上年同期财务数据列表
     * @param list
     */
    void setPreYearFirstToCurr(List<T> list);

    /**
     * 获取上年期初到上年同期财务数据列表
     * @return
     */
    List<T> getPreYearFirstToCurr();

    /**
     * 设置上年1-12月财务数据列表
     * @param list
     */
    void setPreYear(List<T> list);

    /**
     * 获取上年1-12月财务数据列表
     * @return
     */
    List<T> getPreYear();

    /**
     * 设置上上年1-12月财务数据列表
     * @param list
     */
    void setPPreYear(List<T> list);

    /**
     * 获取上上年1-12月财务数据列表
     * @return
     */
    List<T> getPPreYear();
}
