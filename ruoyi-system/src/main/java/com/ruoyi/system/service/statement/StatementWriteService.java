package com.ruoyi.system.service.statement;

import com.ruoyi.system.domain.statement.CellWriter;
import com.ruoyi.system.domain.statement.StmtGenContext;
import com.ruoyi.common.u8c.warpper.FinancialDataWrapper;
import com.ruoyi.common.utils.DateFormatUtil;
import com.ruoyi.common.utils.PeriodUtil;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.statement.RowColHeadIndex;
import com.ruoyi.system.domain.statement.cfg.PeriodWriteCfg;
import com.ruoyi.system.domain.statement.cfg.SheetWriteCfg;
import com.ruoyi.system.domain.statement.cfg.StmtCfgWrapper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 报表写入业务
 */
@Component
public class StatementWriteService {
    private static final Logger log = LoggerFactory.getLogger(StatementWriteService.class);

    /**
     * 坐标写入，按行列头索引映射定位，由提取器提供提取值的逻辑
     * @param tplFile           模板文件
     * @param dataMap           财务数据集
     * @param rcHeadIndexMap    行列头索引映射
     * @param stmtCfg           报表相关配置
     * @param context             扩展上下文
     * @param extractor         提取器
     * @return
     * @param <V>
     * @throws IOException
     */
    public <V> byte[] coordinateWriter(File tplFile, Map<String, FinancialDataWrapper> dataMap,
                                       Map<String, RowColHeadIndex> rcHeadIndexMap,
                                       StmtCfgWrapper stmtCfg,
                                       StmtGenContext context,
                                       ValueExtractor<V> extractor) throws IOException {
        return coordinateWriter(tplFile.toPath(), dataMap, rcHeadIndexMap, stmtCfg, context, extractor);
    }

    /**
     * 坐标写入，按行列头索引映射定位，由提取器提供提取值的逻辑
     * @param tplFile           模板文件路径
     * @param dataMap           财务数据集
     * @param rcHeadIndexMap    行列头索引映射
     * @param stmtCfg           报表相关配置
     * @param context             扩展上下文
     * @param extractor         提取器
     * @return
     * @param <V>
     * @throws IOException
     */
    public <V> byte[] coordinateWriter(String tplFile, Map<String, FinancialDataWrapper> dataMap,
                                       Map<String, RowColHeadIndex> rcHeadIndexMap,
                                       StmtCfgWrapper stmtCfg,
                                       StmtGenContext context,
                                       ValueExtractor<V> extractor) throws IOException {
        return coordinateWriter(Paths.get(tplFile), dataMap, rcHeadIndexMap, stmtCfg, context, extractor);
    }

    /**
     * 坐标写入，按行列头索引映射定位，由提取器提供提取值的逻辑
     * @param tplFile           模板文件路径
     * @param dataMap           财务数据集
     * @param rcHeadIndexMap    行列头索引映射
     * @param stmtCfg           报表相关配置
     * @param context             扩展上下文
     * @param extractor         提取器
     * @return
     * @param <V>
     * @throws IOException
     */
    public <V> byte[] coordinateWriter(Path tplFile, Map<String, FinancialDataWrapper> dataMap,
                                       Map<String, RowColHeadIndex> rcHeadIndexMap,
                                       StmtCfgWrapper stmtCfg,
                                       StmtGenContext context,
                                       ValueExtractor<V> extractor) throws IOException {
        try (InputStream is = Files.newInputStream(tplFile);
             Workbook workbook = WorkbookFactory.create(is)) {
            ByteArrayOutputStream byteOS = new ByteArrayOutputStream();
            processWrite(workbook, dataMap, rcHeadIndexMap, stmtCfg, context, extractor);
            workbook.write(byteOS);
            return byteOS.toByteArray();
        }
    }

    /**
     * 处理写入
     * @param workbook          工作簿
     * @param dataMap           数据集
     * @param rcHeadIndexMap    行列头索引映射
     * @param stmtCfg           报表相关配置
     * @param context             扩展
     * @param extractor         提取器
     * @param <V>
     */
    private <V> void processWrite(Workbook workbook, Map<String, FinancialDataWrapper> dataMap,
                                  Map<String, RowColHeadIndex> rcHeadIndexMap,
                                  StmtCfgWrapper stmtCfg,
                                  StmtGenContext context,
                                  ValueExtractor<V> extractor) {
        List<SheetWriteCfg> writeCfg = stmtCfg.getListWriteCfg();
        if (CollectionUtils.isEmpty(writeCfg)) {
            log.warn("写入配置列表为空");
            return;
        }

        int cfgIndex = 0;
        for (SheetWriteCfg sheetWriteCfg : writeCfg) {
            String sheetKey = sheetWriteCfg.getSheetKey();         // 工作表键
            Integer sheetIndex = sheetWriteCfg.getSheetIndex();    // 工作表索引
            if (sheetIndex < 0 || sheetIndex >= workbook.getNumberOfSheets()) {
                throw new IllegalArgumentException("无效的 sheetIndex: " + sheetIndex);
            }

            FinancialDataWrapper data = dataMap.get(sheetKey);   // 拿到sheetKey对应公司的财务数据
            if (data == null) {
                log.warn("第{}个配置项 sheetKey={} 对应财务数据为空", cfgIndex + 1, sheetKey);
                cfgIndex++;
                continue;
            }

            Sheet sheet = workbook.getSheetAt(sheetIndex);  // 对应工作表

            // 处理写入年份的逻辑
            List<PeriodWriteCfg> periodCfgList = sheetWriteCfg.getPeriodCfgList();
            if (CollectionUtils.isNotEmpty(periodCfgList)) {
                int num = cfgIndex;
                periodCfgList.forEach(cfg -> processPeriodWrite(sheet, cfg, context.period(), num));
            }

            // 获取对应key的行、列索引列表
            RowColHeadIndex rcHeadIndex = rcHeadIndexMap.get(sheetKey);
            if (rcHeadIndex == null) {
                log.warn("第{}个配置项 sheetKey={} 无对应行列头索引", cfgIndex + 1, sheetKey);
                cfgIndex++;
                continue;
            }

            // 处理原值，得到待写入excel中的值的集合
            List<CellWriter<V>> extractList = extractor.extract(data, rcHeadIndex, stmtCfg, cfgIndex, context);
            if (CollectionUtils.isEmpty(extractList)) {
                cfgIndex++;
                continue;
            }
            // 遍历将值设置到单元格中
            extractList.forEach(cw -> setValue(sheet, cw.getRow(), cw.getCol(), cw.getVal(), cw.getFormula()));

            // 强转刷新公式
            workbook.setForceFormulaRecalculation(true);
            cfgIndex++;
        }
    }

    /**
     * 设置值
     * @param sheet 工作表
     * @param rowI  行索引
     * @param colI  列索引
     * @param value  写入值
     * @param isFormula  是否公式
     */
    private void setValue(Sheet sheet, Integer rowI, Integer colI, Object value, Boolean isFormula) {
        if (value == null) return;
        // 确认单元格
        Row row = sheet.getRow(rowI);
        if (row == null) {
            row = sheet.createRow(rowI);
        }
        Cell cell = row.getCell(colI);
        if (cell == null) {
            cell = row.createCell(colI);
        }
        // 公式
        if (isFormula != null && isFormula) {
            cell.setCellFormula((String) value);
        } else {
            // BigDecimal
            if (value instanceof BigDecimal) {
                cell.setCellValue(((BigDecimal) value).doubleValue());
            }
            // String
            else if (value instanceof String) {
                cell.setCellValue((String) value);
            }
            // Integer
            else if (value instanceof Integer) {
                cell.setCellValue((Integer) value);
            }
            // Double
            else if (value instanceof Double) {
                cell.setCellValue((Double) value);
            } else {
                cell.setCellValue("unknown type");
            }
        }
    }

    /**
     * 处理写入年份的逻辑
     * @param sheet      工作表
     * @param cfg       期间写入配置
     * @param period    期间
     */
    private void processPeriodWrite(Sheet sheet, PeriodWriteCfg cfg, YearMonth period, Integer cfgIndex) {
        Boolean isWrite = cfg.getWrite();
        if (isWrite == null || !isWrite) return;

        String dateFormat = cfg.getDateFormat();
        if (StringUtils.isEmpty(dateFormat)) {
            throw new RuntimeException(String.format("第%d个配置项 没有设置dataFormat字段", cfgIndex + 1));
        }
        if (period == null) {
            throw new RuntimeException("期间不能为空");
        }
        List<Integer> writeArea = cfg.getWriteArea();
        if (writeArea.size() != 2) {
            throw new RuntimeException(String.format("第%d个配置项 写入区间不是2位", cfgIndex + 1));
        }

        // 确定单元格
        int row = writeArea.get(0);
        int col = writeArea.get(1);
        Row sheetRow = sheet.getRow(row);
        if (sheetRow == null) {
            sheetRow = sheet.createRow(row);
        }
        Cell cell = sheetRow.getCell(col);
        if (cell == null) {
            cell = sheetRow.createCell(col);
        }

        // 处理中文日期格式
        Boolean isChinese = cfg.getChinese();
        String np = isChinese != null && isChinese
                ? DateFormatUtil.formatToChinese(period, dateFormat)
                : PeriodUtil.toPeriodStr(period);
        String res;
        Integer writeType = cfg.getWriteType();
        if (Objects.equals(0, writeType)) {
            // 覆盖写入
            res = np;
        } else if (Objects.equals(1, writeType)) {
            // 格式化
            res = String.format(cell.getStringCellValue(), np);
        } else {
            res = np;
        }
        cell.setCellValue(res);  // 写入
    }
}
