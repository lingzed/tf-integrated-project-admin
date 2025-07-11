package com.ruoyi.common.enums.statement;

/**
 * 报表类型
 */
public enum StatementType {
    BM_ZJ_LX_CSB("BM_ZJ_LX_CSB", "部门资金利息测算表"),
    CB_JZB("CB_JZB", "成本结转表"),
    GGS_GSF_SR_QK_TJB("GGS_GSF_SR_QK_TJB", "各专业公司各省份收入情况统计表"),
    GL_JYB("GL_JYB", "关联交易表"),
    KH_SZC_FX_MXB("KH_SZC_FX_MXB", "客户收支差分析明细表"),
    KH_YEB("KH_YEB", "客户余额表"),
    LR_FJB("LR_FJB", "利润分解表"),
    RG_CB_RJ_JCB("RG_CB_RJ_JCB", "人工成本及税金检查表"),
    RG_CB_TJB("RG_CB_TJB", "人工成本统计表"),
    YS_ZK_HKL("YS_ZK_HKL", "应收账款回款率"),
    YS_ZX_QK_JKB("YS_ZX_QK_JKB", "预算执行情况监控表"),
    ZN_BM_QSB("ZN_BM_QSB", "职能部门取数表");

    private String statementCode;         // 报表编码
    private String statementName;         // 报表名称

    StatementType(String statementCode, String statementName) {
        this.statementCode = statementCode;
        this.statementName = statementName;
    }

    public String getStatementCode() {
        return statementCode;
    }

    public String getStatementName() {
        return statementName;
    }


    public static StatementType getByCode(String code) {
        for (StatementType type : StatementType.values()) {
            if (type.statementCode.equals(code)) {
                return type;
            }
        }
        return null;
    }
}
