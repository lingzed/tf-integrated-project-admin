package com.ruoyi.common.enums.statement;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 报表模板文件
 */
public enum StatementTpl {
    // 部门资金利息测算表
    TPL_BM_ZJ_LX_CSB_A0501(1, StatementType.BM_ZJ_LX_CSB, "A0501", "部门资金利息测算表A0501-模板.xlsx", ".xlsx", "部门资金利息测算表_A0501_%s.xlsx", "部门资金利息测算表，A0501公司的原始模板"),
    TPL_BM_ZJ_LX_CSB_A0501_SKIP2025M1TOM3(2, StatementType.BM_ZJ_LX_CSB, "A0501", "部门资金利息测算表A0501跳过2025(1-3)-模板.xlsx", ".xlsx", "部门资金利息测算表_A0501_%s.xlsx", "在部门资金利息测算表A0501原始模板的基础上提前设置好25年1-3月的数据，所以跳过1-3月，从4月之后开始"),
    TPL_BM_ZJ_LX_CSB_A0501_SKIP2025M1TOM4(3, StatementType.BM_ZJ_LX_CSB, "A0501", "部门资金利息测算表A0501跳过2025(1-4)-模板.xlsx", ".xlsx", "部门资金利息测算表_A0501_%s.xlsx", "在部门资金利息测算表A0501原始模板的基础上提前设置好25年1-4月的数据，所以跳过1-4月，从5月之后开始"),
    // 成本结转表
    TPL_CB_JZB_A02(4, StatementType.CB_JZB, "A02", "成本结转表A02-模板.xlsx", ".xlsx", "成本结转表_A02_%s.xlsx", "成本结转表，A02公司的模板"),
    // 各专业公司各省份收入情况统计表
    TPL_GGS_GSF_SR_QK_TJB_A(5, StatementType.GGS_GSF_SR_QK_TJB, "A", "各专业公司各省份收入情况统计表-模板.xlsx", ".xlsx", "各专业公司各省份收入情况统计表_%s.xlsx", "各专业公司各省份收入情况统计表模板，表中涵盖所有公司"),
    // 关联交易表
    TPL_GL_JYB_A04(6, StatementType.GL_JYB, "A04", "关联交易表A04-模板.xls", ".xls", "关联交易表_A04_%s.xls", "关联交易表模板，表中涵盖A04所有公司"),
    // 客户收支差分析明细表
    TPL_KH_SZC_FX_MXB_A04(7, StatementType.KH_SZC_FX_MXB, "A04", "客户收支差分析明细表A04-模板.xls", ".xls", "客户收支差分析明细表_A04_%s.xls", "客户收支差分析明细表模板，表中涵盖A04所有公司"),
    // 客户余额表
    TPL_KH_YEB_A04(8, StatementType.KH_YEB, "A04", "客户余额表A04-模板.xls", ".xls", "客户余额表_A04_%s.xls", "客户余额表模板，表中涵盖A04所有公司"),
    // 利润分解表
    TPL_LR_FJB_A01(9, StatementType.LR_FJB, "A01", "利润分解表A01-模板.xlsx", ".xlsx", "利润分解表_A01_%s.xlsx", "利润分解表模板，表中涵盖A01所有公司"),
    TPL_LR_FJB_A02(10, StatementType.LR_FJB, "A02", "利润分解表A02-模板.xlsx", ".xlsx", "利润分解表_A02_%s.xlsx", "利润分解表，A02公司的模板"),
    TPL_LR_FJB_A03(11, StatementType.LR_FJB, "A03", "利润分解表A03-模板.xlsx", ".xlsx", "利润分解表_A03_%s.xlsx", "利润分解表，A03公司的模板"),
    TPL_LR_FJB_A04(12, StatementType.LR_FJB, "A04", "利润分解表A04-模板.xlsx", ".xlsx", "利润分解表_A04_%s.xlsx", "利润分解表模板，表中涵盖A04所有公司"),
    TPL_LR_FJB_A05(13, StatementType.LR_FJB, "A05", "利润分解表A05-模板.xlsx", ".xlsx", "利润分解表_A05_%s.xlsx", "利润分解表模板，表中涵盖A05所有公司"),
    // 人工成本及税金检查表
    TPL_RG_CB_RJ_JCB_A(14, StatementType.RG_CB_RJ_JCB, "A", "人工成本及税金检查表（汇总检查）-模板.xls", ".xls", "人工成本及税金检查表（汇总检查）_%s.xls", "人工成本及税金检查表模板，表中涵盖所有公司"),
    // 人工成本统计表
    TPL_RG_CB_TJB_A(15, StatementType.RG_CB_TJB, "A", "通发公司人工成本统计表-模板.xlsx", ".xlsx", "通发公司人工成本统计表_%s.xlsx", "人工成本统计表模板，表中涵盖所有公司"),
    // 应收账款回款率
    TPL_YS_ZK_HKL_A0501(16, StatementType.YS_ZK_HKL, "A0501", "物业应收账款回款率A0501-模板.xls", ".xls", "物业应收账款回款率_A0501_%s.xls", "应收账款回款率，A0501公司的模板"),
    // 职能部门取数表
    TPL_ZN_BM_QSB_A(17, StatementType.ZN_BM_QSB, "A", "通发公司职能部门取数表A-模板.xls", ".xls", "通发公司职能部门取数表_%s.xls", "职能部门取数表模板，表中涵盖所有公司");

    private Integer tplId;  // 模板文件的id
    private StatementType statementType;    // 报表类型
    private String corpCode;    // 报表模板所属公司编码
    private String tplFilename;    // 模板文件名格式
    private String suffix;  // 文件后缀
    private String outFilename;    // 输出的文件名格式
    private String desc;    // 描述

    StatementTpl(Integer tplId, StatementType statementType, String corpCode, String tplFilename, String suffix, String outFilename, String desc) {
        this.tplId = tplId;
        this.statementType = statementType;
        this.corpCode = corpCode;
        this.tplFilename = tplFilename;
        this.suffix = suffix;
        this.outFilename = outFilename;
        this.desc = desc;
    }

    public Integer getTplId() {
        return tplId;
    }

    public StatementType getStatementType() {
        return statementType;
    }

    public String getCorpCode() {
        return corpCode;
    }

    public String getTplFilename() {
        return tplFilename;
    }

    public String getSuffix() {
        return suffix;
    }

    public String getOutFilename() {
        return outFilename;
    }

    public String getDesc() {
        return desc;
    }

    public static StatementTpl getByTplId(Integer tplId) {
        for (StatementTpl statementTpl : StatementTpl.values()) {
            if (statementTpl.tplId.equals(tplId)) {
                return statementTpl;
            }
        }
        return null;
    }

    public static List<StatementTpl> getByStmtTypeAndCorp(StatementType statementType, String corpCode) {
        return Stream.of(StatementTpl.values())
                .filter(val -> val.getStatementType() == statementType)
                .filter(val -> val.getCorpCode().equals(corpCode))
                .collect(Collectors.toList());
    }

    public static StatementTpl getByName(String name){
        for (StatementTpl statementTpl : StatementTpl.values()) {
            if (statementTpl.name().equals(name)) {
                return statementTpl;
            }
        }
        return null;
    }
}
