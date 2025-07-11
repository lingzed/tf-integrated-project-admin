package com.ruoyi.common.constant;

import com.ruoyi.common.utils.StringUtils;

/**
 * 消息常量
 */
public class MsgConstants {
    public static final String STMT_CFG_CONTENT_IS_EMPTY = "报表配置的内容为空";
    public static final String STMT_CFG_NOT_EXISTS = "报表配置不存在";
    public static final String STMT_CFG_CACHE_CONTENT_IS_EMPTY = "报表配置: %s 缓存的内容为空";
    public static final String UNKNOWN_STMT_CFG_TYPE_V1 = "未知的报表配置类型";
    public static final String UNKNOWN_STMT_CFG_TYPE = UNKNOWN_STMT_CFG_TYPE_V1 + ": %d";
    public static final String STMT_CFG_CODE_EXIST = "配置项编码已存在: %s";
    public static final String CORP_CODE_REQUIRED = "公司编码不能为空";
    public static final String STMT_CODE_REQUIRED = "报表编码不能为空";
    public static final String UNKNOWN_STMT_CODE = "未知的报表编码";
    public static final String UNKNOWN_STMT_CODE_V1 = UNKNOWN_STMT_CODE + ": %s";
    public static final String STMT_CODE_NOT_CHINESE = "报表编码不能包含中文";
    public static final String STMT_NAME_REQUIRED = "报表名称不能为空";
    public static final String CFG_CONT_REQUIRED = "报表的配置内容不能为空";
    public static final String CFG_CONT_TYPE_REQUIRED = "配置内容的Json类型不能为空";
    public static final String JSON_CONT_NOT_LEGAL_FORMAT = "不是合法的Json格式";
    public static final String CFG_CONT_NOT_LEGAL_FORMAT = "配置内容" + JSON_CONT_NOT_LEGAL_FORMAT;
    public static final String UNKNOWN_J_TYPE = "未知json类型";
    public static final String CFG_TYPE_REQUIRED = "配置类型不能为空";
    public static final String ID_REQUIRED = "ID不能为空";
    public static final String ERROR_ID = "错误ID";
    public static final String ID_NOT_EXIST = "ID【%s】不存在";
    public static final String UPDATE_FAILED = "更新失败，请重试";
    public static final String CORP_CODE_NOT_EXISTS = "公司编码不存在";
    public static final String CORP_CODE_NOT_EXISTS_V1 = CORP_CODE_NOT_EXISTS + ": 【%s】";
    public static final String STMT_TPL_FILENAME_REQUIRED = "报表模板文件名为空";
    public static final String STMT_TPL_OUT_FILENAME_REQUIRED = "报表模板输出文件名为空";
    public static final String STMT_TPL_CACHE_KEY_EXISTS = "报表模板文件编码已存在";
    public static final String UNKNOWN_STMT_TPL_ID = "未知的报表模板文件ID";
    public static final String TPL_SUFFIX_NEQ = "上传的模板与原始模板的后缀名不匹配，应为【%s】";
    public static final String TPL_NOT_EXISTS = "模板文件【%s】不存在";
    public static final String TPL_NOT_EXISTS_V1 = "模板文件不存在";
    public static final String DATA_TPL_NOT_EXISTS = "数据模板文件【%s】不存在";
    public static final String FILENAME_REQUIRED = "文件名不能为空";
    public static final String U8C_API_RESPONSE_ERROR = "U8C接口响应异常，异常信息为：%s";
    public static final String SUBJ_CACHE_REFRESH_FAILED = "科目缓存刷新失败，请重试";
    public static final String SUBJ_CACHE_IS_EMPTY = "科目缓存为空";
    public static final String DOWNLOAD_STMT_FILE_NOT_EXISTS = "报表文件不存在";
    public static final String CACHE_STMT_FILE_NAME_NOT_EXISTS = "缓存的报表文件名不存在";
    public static final String RC_HEAD_MAP_DATA_IS_EMPTY = "行列头映射数据集为空";
    public static final String PERIOD_REQUIRED = "期间不能为空";
    public static final String STMT_TPL_FILE_SUFFIX_NOT_EXCEL = "模板文件只支持 [xls, xlsx] 格式";
    public static final String EDIT_FILENAME_REQUIRED = "更新的文件名不能为空";
    public static final String EDIT_FILENAME_SUFFIX_NOT_EQUALS_ORIGINAL = "更新的文件名后缀与上传的文件后缀不一致";
}
