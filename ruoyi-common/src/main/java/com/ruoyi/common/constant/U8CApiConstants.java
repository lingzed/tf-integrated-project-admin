package com.ruoyi.common.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * U8C接口常量
 */
public class U8CApiConstants {
    // 基础地址
    public static final String U8C_API_BASE_ADDRESS = "Http://116.169.58.190:8888";  // 查询辅助余额
    // 接口地址
    public static final String API_URL_QUERY_AUX_BALANCE = U8C_API_BASE_ADDRESS + "/u8cloud/api/gl/voucher/balanceassexquery";  // 查询辅助余额
    public static final String API_URL_QUERY_SUBJECT = U8C_API_BASE_ADDRESS + "/u8cloud/api/uapbd/accsubj/query"; // 查询科目
    public static final String API_URL_QUERY_VOUCHER = U8C_API_BASE_ADDRESS + "/u8cloud/api/gl/voucher/pagequery"; // 查询凭证
    public static final String API_URL_QUERY_SUBJ_BALANCE = U8C_API_BASE_ADDRESS + "/u8cloud/api/gl/voucher/balancebookquery"; // 查询科目余额
    public static final String API_URL_QUERY_CUSTOMER = U8C_API_BASE_ADDRESS + "/u8cloud/api/uapbd/custdoc/query"; // 客户查询
    public static final String API_URL_QUERY_JOB_BAS_FIL = U8C_API_BASE_ADDRESS + "/u8cloud/api/uapbd/bdjobbasfil/query"; // 项目查询
    // 请求必须携带的头
    public static final String REQUEST_HEAD_USER_CODE = "usercode"; // 请求头usercode
    public static final String HEAD_VAL_USER_CODE = "002"; // usercode的值
    public static final String REQUEST_HEAD_PASSWORD = "password"; // 请求头password
    public static final String HEAD_VAL_PASSWORD = "e10adc3949ba59abbe56e057f20f883e"; // password的值
    public static final String REQUEST_HEAD_SYSTEM = "system"; // 请求头system
    public static final String HEAD_VAL_SYSTEM = "DSF002"; // system的值
    public static final Map<String, String> U8C_API_BASE_REQUEST_HEADS = new HashMap<>(); // U8CApi基础请求头
    static {
        U8C_API_BASE_REQUEST_HEADS.put(REQUEST_HEAD_USER_CODE, HEAD_VAL_USER_CODE);
        U8C_API_BASE_REQUEST_HEADS.put(REQUEST_HEAD_PASSWORD, HEAD_VAL_PASSWORD);
        U8C_API_BASE_REQUEST_HEADS.put(REQUEST_HEAD_SYSTEM, HEAD_VAL_SYSTEM);
    }

}
