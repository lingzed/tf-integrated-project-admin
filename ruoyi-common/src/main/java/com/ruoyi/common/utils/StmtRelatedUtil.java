package com.ruoyi.common.utils;

import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.constant.CacheConstants;
import com.ruoyi.common.constant.MsgConstants;
import com.ruoyi.common.enums.statement.StatementCfgType;
import com.ruoyi.common.enums.statement.StatementTpl;
import com.ruoyi.common.enums.statement.StatementType;
import com.ruoyi.common.exception.ServiceException;

import java.nio.file.Paths;

/**
 * 报表相关工具类
 */
public class StmtRelatedUtil {
    /**
     * 构建报表配置的编码
     * @param stmtCode  报表编码
     * @param cfgType   报表配置类型
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
     * 构建报表模板文件的编码
     * @param stmtCode  报表编码
     * @param corpCode  公司编码
     * @param other     其他
     * @return
     */
    private static String buildTplCode(String stmtCode, String corpCode, String other) {
        if (StringUtils.isEmpty(other)) {
            return String.format(CacheConstants.STMT_TPL_KEY_TEMPLATE, stmtCode, corpCode);
        } else {
            return String.format(CacheConstants.STMT_TPL_KEY_TEMPLATE_OTHER, stmtCode, corpCode, other);
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
     * @param stmtTpl       报表模板文件枚举
     * @param stmtCfgType   报表配置类型枚举
     * @return
     */
    public static String getCfgCode(StatementTpl stmtTpl, StatementCfgType stmtCfgType) {
        String statementCode = stmtTpl.getStatementType().getStatementCode();
        return buildCfgCode(statementCode, stmtCfgType.getCfgType(), stmtTpl.getCorpCode(), null);
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

    /**
     * 获取报表配置的编码
     * @param stmtCode      报表编码
     * @param cfgType       配置类型编码
     * @param corpCode      公司编码
     * @return
     */
    public static String getCfgCode(String stmtCode, Short cfgType, String corpCode) {
        return buildCfgCode(stmtCode, cfgType, corpCode, null);
    }

    /**
     * 获取报表模板文件的编码
     * @param stmtCode  报表类型编码
     * @param corpCode  公司编码
     * @param other     其他
     * @return
     */
    public static String getTplCode(String stmtCode, String corpCode, String other) {
        return buildTplCode(stmtCode, corpCode, other);
    }

    /**
     * 获取报表模板文件的编码
     * @param stmtCode  报表类型编码
     * @param corpCode  公司编码
     * @return
     */
    public static String getTplCode(String stmtCode, String corpCode) {
        return buildTplCode(stmtCode, corpCode, null);
    }

    /**
     * 获取报表模板文件的编码
     * @param stmtType  报表类型枚举
     * @param corpCode  公司编码
     * @param other     其他
     * @return
     */
    public static String getTplCode(StatementType stmtType, String corpCode, String other) {
        return buildTplCode(stmtType.getStatementCode(), corpCode, other);
    }

    /**
     * 获取报表模板文件的编码
     * @param stmtType  报表类型枚举
     * @param corpCode  公司编码
     * @return
     */
    public static String getTplCode(StatementType stmtType, String corpCode) {
        return buildTplCode(stmtType.getStatementCode(), corpCode, null);
    }

    /**
     * 获取报表模板文件路径
     * @param statementTpl
     * @return
     */
    public static String getStmtTplFile(StatementTpl statementTpl) {
        if (statementTpl == null) {
            throw new ServiceException(MsgConstants.TPL_NOT_EXISTS_V1);
        }
        String statementTempPath = RuoYiConfig.getStatementTempPath(statementTpl.getStatementType());
        return Paths.get(statementTempPath, statementTpl.getTplFilename()).toAbsolutePath().toString();
    }

    /**
     * 获取报表数据模板文件路径格式
     * 此方法返回的数据模板文件名是StatementTpl中的outFilename，也就是输出文件名的格式
     * 因此通过此方法拿到的文件路径实际并非真实文件路径，而是带有占位符的文件路径格式，需要进行format操作
     * @param statementTpl
     * @return
     */
    public static String getStmtDataTplFile(StatementTpl statementTpl) {
        if (statementTpl == null) {
            throw new ServiceException(MsgConstants.TPL_NOT_EXISTS_V1);
        }
        String dataTplPath = RuoYiConfig.getStatementDataTempPath(statementTpl.getStatementType());
        return Paths.get(dataTplPath, statementTpl.getOutFilename()).toAbsolutePath().toString();
    }
}
