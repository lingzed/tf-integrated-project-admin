package com.ruoyi.common.u8c.cust;

/**
 * 客户实体
 */
public class Customer {
    private String custcode;    // 客户编码
    private String custname;    // 客户名称

    public String getCustcode() {
        return custcode;
    }

    public void setCustcode(String custcode) {
        this.custcode = custcode;
    }

    public String getCustname() {
        return custname;
    }

    public void setCustname(String custname) {
        this.custname = custname;
    }
}
