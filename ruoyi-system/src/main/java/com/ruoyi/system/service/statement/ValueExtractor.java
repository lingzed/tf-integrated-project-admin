package com.ruoyi.system.service.statement;

import com.ruoyi.system.domain.statement.CellWriter;
import com.ruoyi.system.domain.statement.StmtGenContext;
import com.ruoyi.common.u8c.warpper.FinancialDataWrapper;
import com.ruoyi.system.domain.statement.RowColHeadIndex;
import com.ruoyi.system.domain.statement.cfg.StmtCfgWrapper;

import java.util.List;

/**
 * 值提取器接口，用于根据行列索引、期间和配置等信息，
 * 从财务数据集中提取用于报表写入的单元格值。
 *
 * @param <V> 单元格中写入值的类型（如 BigDecimal、String 等）
 */
public interface ValueExtractor<V> {
    /**
     * 从财务数据集中提取写入报表所需的单元格值。
     * 每个提取结果包括写入值及其对应的行列坐标，封装为 CellWriter 对象。
     *
     * @param data   财务数据，封装了辅助余额、科目余额、凭证等信息
     * @param rcHeadIndex 报表的行列头索引，包含行名/列名与实际坐标的映射
     * @param stmtCfg    报表配置包装等
     * @param index     索引
     * @param context     扩展上下文
     * @return 单元格写入对象集合，每个对象包含值及其写入的行列坐标
     */
    List<CellWriter<V>> extract(FinancialDataWrapper data, RowColHeadIndex rcHeadIndex, StmtCfgWrapper stmtCfg,
                                Integer index, StmtGenContext context);
}
