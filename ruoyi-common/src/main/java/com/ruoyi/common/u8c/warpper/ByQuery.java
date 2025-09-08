package com.ruoyi.common.u8c.warpper;

import java.util.List;

/**
 * 查询维度
 */
public interface ByQuery<T> {
    String MAIN = "主查询";    // 本期
    String SUPPLEMENT = "补充查询";    // 上期

    /**
     * 设置主查询财务数据
     * @param list
     */
    void setMain(List<T> list);

    /**
     * 获取主查询财务数据列表
     */
    List<T> getMain();

    /**
     * 设置补充查询财务数据
     * @param list
     */
    void setSupplement(List<T> list);

    /**
     * 获取补充查询财务数据列表
     */
    List<T> getSupplement();
}
