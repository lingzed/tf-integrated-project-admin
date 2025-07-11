package com.ruoyi.system.service.statement;

import com.ruoyi.system.domain.statement.StmtGenContext;
import com.ruoyi.common.u8c.warpper.FinancialDataWrapper;
import com.ruoyi.system.domain.statement.RowColHeadIndex;
import com.ruoyi.system.domain.statement.cfg.StmtCfgWrapper;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * 报表处理的接口
 */
public interface StatementProcess {
    /**
     * 确定报表的模板文件<br>
     * 返回对应的报表模板文件枚举项名称，将通过这个名称获取StatementTpl对象
     * @param context   扩展上下文
     * @return
     */
    String confirmStmtTplName(StmtGenContext context);

    /**
     * 加载报表相关配置
     * @param context    扩展上下文
     * @return
     */
    StmtCfgWrapper loadStmtCfg(StmtGenContext context);


    /**
     * 从模板文件中获取行列头索引映射集合
     * 其中：<br>
     * key为模板文件中一个sheet的标识，对应的RowColHeadIndex为此sheet中的行列头索引映射
     * @param stmtTplFile   报表模板文件
     * @param stmtCfgWrapper  报表相关配置
     * @param context    扩展上下文
     * @return
     */
    Map<String, RowColHeadIndex> getRowColHeadIndexMap(File stmtTplFile, StmtCfgWrapper stmtCfgWrapper, StmtGenContext context) throws IOException;

    /**
     * 控制是否执行提取行列头映射数据集的流程
     * 返回true执行，false不执行
     * @param context
     * @return
     */
    Boolean withDataTplHandle(StmtGenContext context);

    /**
     * 获取报表的数据模板文件路径
     * @return
     */
    String getStmtDataTplFilepath(StmtGenContext context);

    /**
     * 从数据模板文件中解析行列头对应的数据集。
     *
     * <p>返回的 Map 结构为：<br>
     * - 外层 Map 的 key 表示行头值（Row Header）<br>
     * - 内层 Map 的 key 表示列头值（Column Header），value 为对应的单元格数据
     *
     * @param dataTplFile 报表数据模板文件
     * @param stmtCfgWrapper 报表相关配置
     * @param context 扩展上下文
     * @return 行头与列头对应的数据映射表
     */
    Map<String, Map<String, String>> getRowColHeadMapData(File dataTplFile, StmtCfgWrapper stmtCfgWrapper, StmtGenContext context) throws IOException;

    /**
     * 调用U8C接口拉取财务数据并建立映射结果。
     *
     * <p>每个 key 对应一个 sheetKey（即工作表标识），value 为该工作表对应的财务数据包装对象 {@link FinancialDataWrapper}。
     * <p>{@link FinancialDataWrapper} 用于封装一组与该工作表相关的财务数据，例如：科目余额、辅助余额、凭证等，支持单独或组合返回。
     *
     * @param rcHeadIndexMap 行列头位置信息映射，用于解析各 sheetKey 的写入配置
     * @param stmtCfgWrapper 报表相关配置
     * @param context 扩展上下文
     * @return 包含各 sheetKey 对应财务数据的映射关系
     */
    Map<String, FinancialDataWrapper> fetchData(Map<String, RowColHeadIndex> rcHeadIndexMap, StmtCfgWrapper stmtCfgWrapper,
                                                StmtGenContext context);

    /**
     * 对扩展上下文对象进行扩展操作
     * @param context
     */
    void doExpandContext(StmtGenContext context);

    /**
     * 数据写入字节数组
     * @param stmtTplFile   模板文件
     * @param rcHeadIndexMap   行列头索引映射
     * @param dataMap   远程拉取的数据集
     * @param context   扩展上下文
     * @return
     */
    byte[] writeData(File stmtTplFile, Map<String, RowColHeadIndex> rcHeadIndexMap,
                     Map<String, FinancialDataWrapper> dataMap, StmtCfgWrapper stmtCfg, StmtGenContext context) throws IOException;

    /**
     * 输出文件，返回URL
     * @param dataByte   数据字节数组
     * @param outFile   输出文件名，以通过当前期间格式化
     * @param context   扩展上下文
     */
    String outputFile(byte[] dataByte, String outFile, StmtGenContext context) throws IOException;

    /**
     * 保存数据模板文件
     * @param dataByte      数据字节数组
     * @param outFile       输出文件名，以通过当前期间格式化
     * @param context       扩展上下文
     * @return
     * @throws IOException
     */
    String saveDataTpl(byte[] dataByte, String outFile, StmtGenContext context) throws IOException;
}
