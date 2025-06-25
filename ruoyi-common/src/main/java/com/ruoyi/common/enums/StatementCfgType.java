package com.ruoyi.common.enums;

/**
 * 报表配置的类型枚举
 */
public enum StatementCfgType {
    ROW_COL_HEAD_INDEX_CFG(0, "行列头索引配置", "com.ruoyi.system.domain.statementcfg.RowColHeadIndexCfg"),
    SHEET_WRITE_CFG(1, "工作表写入配置", "com.ruoyi.system.domain.statementcfg.SheetWriteCfg"),
    STATEMENT_QUERY_CFG(2, "报表查询配置", "com.ruoyi.system.domain.statementcfg.StatementQueryCfg"),
    ROW_COL_HEAD_DATA_MAPPER_CFG(3, "行列头映射数据集配置", "com.ruoyi.system.domain.statementcfg.RowColHeadDataMapperCfg");

    private Integer cfgType;
    private String desc;
    private String className;

    StatementCfgType(Integer cfgType, String desc, String className) {
        this.cfgType = cfgType;
        this.desc = desc;
        this.className = className;
    }

    public Integer getCfgType() {
        return cfgType;
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
    public StatementCfgType getByType(Integer type) {
        for (StatementCfgType scType : StatementCfgType.values()) {
            if (scType.getCfgType() == type) return scType;
        }
        return null;
    }
}
