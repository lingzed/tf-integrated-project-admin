package com.ruoyi.common.enums.statement;

import com.ruoyi.common.exception.ServiceException;

/**
 * 报表配置的类型枚举
 */
public enum StatementCfgType {
    ROW_COL_HEAD_INDEX_CFG((short) 0, "行列头索引配置", "行列头索引配置", "com.ruoyi.system.domain.statementcfg.RowColHeadIndexCfg"),
    SHEET_WRITE_CFG((short) 1, "工作表写入配置", "工作表写入配置", "com.ruoyi.system.domain.statementcfg.SheetWriteCfg"),
    STATEMENT_QUERY_CFG((short) 2, "报表查询配置", "报表查询配置", "com.ruoyi.system.domain.statementcfg.StatementQueryCfg"),
    ROW_COL_HEAD_DATA_MAPPER_CFG((short) 3, "行列头映射数据集配置", "行列头映射数据集配置", "com.ruoyi.system.domain.statementcfg.RowColHeadDataMapperCfg");

    private Short cfgType;
    private String typeName;
    private String desc;
    private String className;

    StatementCfgType(Short cfgType, String typeName, String desc, String className) {
        this.cfgType = cfgType;
        this.typeName = typeName;
        this.desc = desc;
        this.className = className;
    }

    public Short getCfgType() {
        return cfgType;
    }

    public String getTypeName() {
        return typeName;
    }

    public String getDesc() {
        return desc;
    }

    public String getClassName() {
        return className;
    }

    /**
     * 通过type获取报表配置类型枚举
     * @param type
     * @return
     */
    public static StatementCfgType getByType(Short type) {
        for (StatementCfgType scType : StatementCfgType.values()) {
            if (scType.getCfgType().equals(type)) return scType;
        }
        return null;
    }
}
