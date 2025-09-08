package com.ruoyi.system.service.statement.impl;

import com.alibaba.fastjson2.JSON;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.system.domain.statement.StmtGenContext;
import com.ruoyi.common.u8c.warpper.FinancialDataWrapper;
import com.ruoyi.common.utils.StmtRelatedUtil;
import com.ruoyi.common.utils.file.FileUtils;
import com.ruoyi.common.utils.uuid.IdUtils;
import com.ruoyi.system.domain.statement.RowColHeadIndex;
import com.ruoyi.common.enums.statement.StatementTpl;
import com.ruoyi.system.domain.statement.cfg.StmtCfgWrapper;
import com.ruoyi.system.service.statement.StatementProcess;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Collections;
import java.util.Map;

/**
 * 报表生成处理
 */
public abstract class StatementGenProcess implements StatementProcess {
    private static final Logger log = LoggerFactory.getLogger(StatementGenProcess.class);

    @Resource
    private RedisCache redisCache;

    /**
     * 确定报表的模板文件<br>
     * 返回对应的报表模板文件枚举项名称，将通过这个名称获取StatementTpl对象
     *
     * @param context 扩展上下文
     * @return
     */
    @Override
    public abstract String confirmStmtTplName(StmtGenContext context);

    /**
     * 通过报表模板文件枚举项名称返回StatementTpl
     *
     * @param context
     * @return
     */
    public final StatementTpl getStmtTpl(StmtGenContext context) {
        String stmtTplName = confirmStmtTplName(context);
        StatementTpl byName = StatementTpl.getByName(stmtTplName);
        if (byName == null) {
            throw new RuntimeException(String.format("未知的报表模板：【%s】", stmtTplName));
        }
        return byName;
    }

    /**
     * 获取报表的模板文件
     *
     * @param statementTpl
     * @param context
     * @return
     */
    public final File getStmtTplFile(StatementTpl statementTpl, StmtGenContext context) {
        String stmtTplFile = StmtRelatedUtil.getStmtTplFile(statementTpl);
        Path path = Paths.get(stmtTplFile);
        if (Files.notExists(path)) {
            throw new RuntimeException(String.format("模板文件：【%s】不存在", path.getFileName()));
        }
        return path.toFile();
    }

    /**
     * 加载报表相关配置
     *
     * @param context 扩展上下文
     * @return
     */
    @Override
    public abstract StmtCfgWrapper loadStmtCfg(StmtGenContext context);

    public final void checkStmtCfg(StmtCfgWrapper stmtCfg) {
    }

    /**
     * 从模板文件中获取行列头索引映射集合
     * 其中：<br>
     * key为模板文件中一个sheet的标识，对应的RowColHeadIndex为此sheet中的行列头索引映射
     *
     * @param stmtTplFile    报表模板文件
     * @param stmtCfgWrapper 报表相关配置
     * @param context        扩展上下文
     * @return
     */
    @Override
    public abstract Map<String, RowColHeadIndex> getRowColHeadIndexMap(File stmtTplFile, StmtCfgWrapper stmtCfgWrapper,
                                                                       StmtGenContext context) throws IOException;

    /**
     * 控制是否执行提取行列头映射数据集的流程<br>
     * 返回true执行，false不执行
     * 默认返回false
     *
     * @param context
     * @return
     */
    @Override
    public Boolean withDataTplHandle(StmtGenContext context) {
        return false;
    }

    /**
     * 检查行列头索引映射
     *
     * @param rcHeadIndexMap
     */
    public final void checkRcHeadIndex(Map<String, RowColHeadIndex> rcHeadIndexMap) {
        if (MapUtils.isEmpty(rcHeadIndexMap)) {
            throw new RuntimeException("行列头索引映射为空");
        }
        log.info("行列头索引映射: {}", JSON.toJSONString(rcHeadIndexMap));
    }

    /**
     * 获取报表的数据模板文件路径<br>
     * 文件名默认为模板文件的输出文件名outFilename<br>
     * 文件名格式为原始格式，还未进行日期格式化
     *
     * @return
     */
    @Override
    public String getStmtDataTplFilepath(StmtGenContext context) {
        return StmtRelatedUtil.getStmtDataTplFile(context.statementTpl());
    }

    /**
     * 获取报表的数据模板文件
     *
     * @param context
     * @return
     */
    public final File getStmtDataTplFile(StmtGenContext context) {
        Path path = Paths.get(getStmtDataTplFilepath(context));
//        log.info("数据模板文件: {}", path.toString());
        if (Files.notExists(path)) {
            throw new RuntimeException(String.format("数据模板文件：【%s】不存在", path.getFileName()));
        }
        return path.toFile();
    }

    /**
     * 从数据模板文件中解析行列头对应的数据集。
     * <p>返回的 Map 结构为：<br>
     * - 外层 Map 的 key 表示行头值（Row Header）<br>
     * - 内层 Map 的 key 表示列头值（Column Header），value 为对应的单元格数据<br>
     * 默认返回空集合
     *
     * @param dataTplFile    报表数据模板文件
     * @param stmtCfgWrapper 报表相关配置
     * @param context        扩展上下文
     * @return 行头与列头对应的数据映射表
     */
    @Override
    public Map<String, Map<String, String>> getRowColHeadMapData(File dataTplFile, StmtCfgWrapper stmtCfgWrapper,
                                                                 StmtGenContext context) throws IOException {
        return Collections.emptyMap();
    }

    /**
     * 调用U8C接口拉取财务数据并建立映射结果。
     *
     * <p>每个 key 对应一个 sheetKey（即工作表标识），value 为该工作表对应的财务数据包装对象 {@link FinancialDataWrapper}。
     * <p>{@link FinancialDataWrapper} 用于封装一组与该工作表相关的财务数据，例如：科目余额、辅助余额、凭证等，支持单独或组合返回。
     *
     * @param rcHeadIndexMap 行列头位置信息映射，用于解析各 sheetKey 的写入配置
     * @param stmtCfgWrapper 报表相关配置
     * @param context        扩展上下文
     * @return 包含各 sheetKey 对应财务数据的映射关系
     */
    @Override
    public abstract Map<String, FinancialDataWrapper> fetchData(Map<String, RowColHeadIndex> rcHeadIndexMap,
                                                                StmtCfgWrapper stmtCfgWrapper,
                                                                StmtGenContext context);

    /**
     * 对扩展上下文对象进行扩展操作<br>
     * 时机在请求方法fetchData()执行之后<br>
     * 在写入方法writeData()执行之前
     *
     * @param context
     */
    @Override
    public void doExpandContext(StmtGenContext context) {

    }

    /**
     * 数据写入字节数组
     *
     * @param stmtTplFile    模板文件
     * @param rcHeadIndexMap 行列头索引映射
     * @param dataMap        远程拉取的数据集
     * @param context        扩展上下文
     * @return
     */
    @Override
    public abstract byte[] writeData(File stmtTplFile, Map<String, RowColHeadIndex> rcHeadIndexMap,
                                     Map<String, FinancialDataWrapper> dataMap,
                                     StmtCfgWrapper stmtCfg,
                                     StmtGenContext context) throws IOException;

    /**
     * 输出文件，返回URL
     * 提供一个默认实现
     *
     * @param dataByte 数据字节数组
     * @param outFile  输出文件名，以通过当前期间格式化
     * @param context  扩展上下文
     */
    @Override
    public String outputFile(byte[] dataByte, String outFile, StmtGenContext context) throws IOException {
        return outputFile(dataByte, outFile);
    }

    public String outputFile(byte[] dataByte, String outFile) throws IOException {
        // 缓存文件id对应的文件名
        String fileId = IdUtils.fastSimpleUUID();
        redisCache.setCacheStr(fileId, outFile);
        redisCache.expire(fileId, 600); // 有效期10min

        String tempFile = fileId + FileUtils.getNameSuffix(outFile); // 临时报表文件名
        Path genRootPath = Paths.get(RuoYiConfig.getStatementGenRootPath());    // 临时报表根目标，不存在则创建
        if (Files.notExists(genRootPath)) {
            Files.createDirectories(genRootPath);
        }
        Path file = genRootPath.resolve(tempFile);

        // CREATE: 如果文件不存在则创建
        // TRUNCATE_EXISTING: 如果文件已存在就清空再写
        Files.write(file, dataByte, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        return "/stmt-gen/download/" + fileId;
    }

    /**
     * 保存数据模板文件<br>
     * 此方法默认不保存数据模板文件<br>
     * 若要保存需重写
     *
     * @param dataByte 数据模板文件的字节数组
     * @param outFile  输出文件名，以通过当前期间格式化
     * @param context  扩展上下文
     * @return
     * @throws IOException
     */
    @Override
    public String saveDataTpl(byte[] dataByte, String outFile, StmtGenContext context) throws IOException {
        return "";
    }
}
