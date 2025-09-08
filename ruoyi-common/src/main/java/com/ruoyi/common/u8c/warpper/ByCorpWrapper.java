package com.ruoyi.common.u8c.warpper;

import java.util.List;

/**
 * 通过公司包装
 * @param <T>
 */
public interface ByCorpWrapper<T> {
    /**
     * 通过公司编码设置财务数据列表
     * @param corpCode
     * @param list
     */
    void setByCorpCode(String corpCode, List<T> list);

    /**
     * 通过公司编码获取财务数据列表
     * @param corpCode
     * @return
     */
    List<T> getByCorpCode(String corpCode);
}
