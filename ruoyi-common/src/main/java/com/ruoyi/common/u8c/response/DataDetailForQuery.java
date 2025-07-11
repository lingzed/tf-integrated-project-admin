package com.ruoyi.common.u8c.response;

import java.util.List;

/**
 * U8C查询类接口响应实体中的data字段的类型
 */
public class DataDetailForQuery {
    private String allcount;    // 总数
    private String retcount;    // 返回数
    private String datas;      // 数据列表

    public String getAllcount() {
        return allcount;
    }

    public void setAllcount(String allcount) {
        this.allcount = allcount;
    }

    public String getRetcount() {
        return retcount;
    }

    public void setRetcount(String retcount) {
        this.retcount = retcount;
    }

    public String getDatas() {
        return datas;
    }

    public void setDatas(String datas) {
        this.datas = datas;
    }
}
