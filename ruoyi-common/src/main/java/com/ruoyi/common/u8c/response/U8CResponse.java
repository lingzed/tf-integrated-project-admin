package com.ruoyi.common.u8c.response;

/**
 * U8C响应实体
 */
public class U8CResponse {
    private String status;      // 状态
    private String data;        // 数据
    private String errorcode;   // 错误码
    private String errormsg;   // 错误信息
    private String taskNumber;  // 调用次数

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getErrorcode() {
        return errorcode;
    }

    public void setErrorcode(String errorcode) {
        this.errorcode = errorcode;
    }

    public String getErrormsg() {
        return errormsg;
    }

    public void setErrormsg(String errormsg) {
        this.errormsg = errormsg;
    }

    public String getTaskNumber() {
        return taskNumber;
    }

    public void setTaskNumber(String taskNumber) {
        this.taskNumber = taskNumber;
    }
}
