package com.ruoyi.common.utils;

import com.ruoyi.common.constant.CacheConstants;
import com.ruoyi.common.enums.statement.StatementCfgType;
import com.ruoyi.common.enums.statement.StatementType;

/**
 * 报表配置项的编码构建工具类
 */
public class StmtCfgUtil {
    /**
     * 构建报表配置的编码
     * @param stmtCode  报表编码
     * @param cfgType   报表类型编码
     * @param corpCode  公司编码
     * @param other     其他
     * @return
     */
    private static String buildCfgCode(String stmtCode, Short cfgType, String corpCode, String other) {
        if (StringUtils.isEmpty(other)) {
            return String.format(CacheConstants.STMT_CFG_KEY_TEMPLATE, stmtCode, corpCode, cfgType);
        } else {
            return String.format(CacheConstants.STMT_CFG_KEY_TEMPLATE_OTHER, stmtCode, corpCode, cfgType, other);
        }
    }

    /**
     * 获取报表配置的编码
     * @param stmtType      报表类型枚举
     * @param stmtCfgType   报表配置类型
     * @param corpCode      公司编码
     * @param other         其他
     * @return
     */
    public static String getCfgCode(StatementType stmtType, StatementCfgType stmtCfgType, String corpCode, String other) {
        return buildCfgCode(stmtType.getStatementCode(), stmtCfgType.getCfgType(), corpCode, other);
    }

    /**
     * 获取报表配置的编码
     * @param stmtType      报表类型枚举
     * @param stmtCfgType   报表配置类型
     * @param corpCode      公司编码
     * @return
     */
    public static String getCfgCode(StatementType stmtType, StatementCfgType stmtCfgType, String corpCode) {
        return buildCfgCode(stmtType.getStatementCode(), stmtCfgType.getCfgType(), corpCode, null);
    }

    /**
     * 获取报表配置的编码
     * @param stmtCode      报表编码
     * @param cfgType       配置类型编码
     * @param corpCode      公司编码
     * @param other         其他
     * @return
     */
    public static String getCfgCode(String stmtCode, Short cfgType, String corpCode, String other) {
        return buildCfgCode(stmtCode, cfgType, corpCode, other);
    }
}
