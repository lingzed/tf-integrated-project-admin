package com.ruoyi.common.constant;

/**
 * 缓存的key 常量
 *
 * @author ruoyi
 */
public class CacheConstants {
    /**
     * 登录用户 redis key
     */
    public static final String LOGIN_TOKEN_KEY = "login_tokens:";

    /**
     * 验证码 redis key
     */
    public static final String CAPTCHA_CODE_KEY = "captcha_codes:";

    /**
     * 参数管理 cache key
     */
    public static final String SYS_CONFIG_KEY = "sys_config:";

    /**
     * 字典管理 cache key
     */
    public static final String SYS_DICT_KEY = "sys_dict:";

    /**
     * 防重提交 redis key
     */
    public static final String REPEAT_SUBMIT_KEY = "repeat_submit:";

    /**
     * 限流 redis key
     */
    public static final String RATE_LIMIT_KEY = "rate_limit:";

    /**
     * 登录账户密码错误次数 redis key
     */
    public static final String PWD_ERR_CNT_KEY = "pwd_err_cnt:";

    /**
     * 报表配置缓存的key模板
     * 格式: stmt_cfg:报表编码:公司编码:报表配置类型编码
     */
    public static final String STMT_CFG_KEY_TEMPLATE = "stmt_cfg:%s:%s:%d";

    /**
     * 报表配置缓存的key模板(含其他)
     * 格式: stmt_cfg:报表编码:公司编码:报表配置类型编码:other
     */
    public static final String STMT_CFG_KEY_TEMPLATE_OTHER = "stmt_cfg:%s:%s:%d:%s";

    /**
     * 报表配置缓存的key模板
     * 格式: stmt_tpl:报表编码:公司编码
     */
    public static final String STMT_TPL_KEY_TEMPLATE = "stmt_tpl:%s:%s";

    /**
     * 报表配置缓存的key模板(含其他)
     * 格式: stmt_tpl:报表编码:公司编码:other
     */
    public static final String STMT_TPL_KEY_TEMPLATE_OTHER = "stmt_tpl:%s:%s:%s";

    /**
     * 所有科目的 redis key
     */
    public static final String ALL_SUBJ_CACHE_KEY = "all_subj:";

    /**
     * SSE临时票据的 redis key
     */
    public static final String SSE_TICKET_CACHE_KEY = "sse:ticket:";
}
