package com.ruoyi.system.service.statement;

import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.statement.cfg.ColHeadCfg;
import com.ruoyi.system.domain.statement.cfg.RowColHeadDataMapperCfg;
import com.ruoyi.system.domain.statement.cfg.RowColHeadIndexCfg;
import com.ruoyi.system.domain.statement.cfg.RowHeadCfg;
import com.ruoyi.system.domain.statement.RowColHeadIndex;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 报表读取业务
 */
@Service
public class StatementReadService {
    private static final Logger log = LoggerFactory.getLogger(StatementReadService.class);
    // 从括号中取值的正则
    private static final Pattern PATTERN1 = Pattern.compile("(?:（|\\()([^（）()]+)(?:）|\\))");
    // 获取-之前的正则
//    private static final Pattern PATTERN2 = Pattern.compile("^[^-]+");
    // 从【】中取值的正则
    private static final Pattern PATTERN3 = Pattern.compile("【([^【】]+)】");

    /**
     * 从报表模板文件中提取出行列头索引的映射
     * 其中key为RowColHeadIndexCfg配置中的sheetKey
     * value为行列头映射
     * @param stmtTplFile   模板文件
     * @param cfgList       行列头映射配置列表
     * @return
     * @throws Exception
     */
    public Map<String, RowColHeadIndex> extractRowColHead(File stmtTplFile, List<RowColHeadIndexCfg> cfgList) throws IOException {
        return extractRowColHead(stmtTplFile.toPath(), cfgList);
    }

    /**
     * 从报表模板文件中提取出行列头索引的映射
     * 其中key为RowColHeadIndexCfg配置中的sheetKey
     * value为行列头映射
     * @param stmtTplFile   模板文件路径
     * @param cfgList       行列头映射配置列表
     * @return
     * @throws Exception
     */
    public Map<String, RowColHeadIndex> extractRowColHead(String stmtTplFile, List<RowColHeadIndexCfg> cfgList) throws IOException {
        return extractRowColHead(Paths.get(stmtTplFile), cfgList);
    }

    /**
     * 从报表模板文件中提取出行列头索引的映射
     * 其中key为RowColHeadIndexCfg配置中的sheetKey
     * value为行列头映射
     * @param stmtTplFile   模板文件路径对象
     * @param cfgList       行列头映射配置列表
     * @return
     * @throws Exception
     */
    public Map<String, RowColHeadIndex> extractRowColHead(Path stmtTplFile, List<RowColHeadIndexCfg> cfgList) throws IOException {
        Map<String, RowColHeadIndex> result = new HashMap<>();

        try (InputStream is = Files.newInputStream(stmtTplFile);
             Workbook workbook = WorkbookFactory.create(is)) {

            cfgList.forEach(cfg -> {
                Integer sheetIndex = cfg.getSheetIndex();   // 工作表索引
                if (sheetIndex == null) return;
                Sheet sheet = workbook.getSheetAt(sheetIndex);    // 对应工作表

                List<RowHeadCfg> rowHeadCfgList = cfg.getRowHeadCfgList();  // 行头配置列表
                Map<String, Integer> rowHeadIdx = processRowHeadCfg(rowHeadCfgList, sheet);

                List<ColHeadCfg> colHeadCfgList = cfg.getColHeadCfgList();  // 列头配置列表
                Map<String, Integer> colHeadIdx = processColHeadCfg(colHeadCfgList, sheet);

                RowColHeadIndex rcHead = new RowColHeadIndex();
                rcHead.setRowHeadIdx(rowHeadIdx);
                rcHead.setColHeadIdx(colHeadIdx);

                result.put(cfg.getSheetKey(), rcHead);
            });
        }

        return result;
    }

    /**
     * 行头配置处理方法
     * @param rowHeadCfgList
     * @param sheet
     * @return
     */
    private Map<String, Integer> processRowHeadCfg(List<RowHeadCfg> rowHeadCfgList, Sheet sheet) {
        if (CollectionUtils.isEmpty(rowHeadCfgList)) return null;
        HashMap<String, Integer> res = new HashMap<>();

        rowHeadCfgList.forEach(rowHeadCfg -> {
            Integer rowHdStartRowI = rowHeadCfg.getRowHdStartRowI();    // 行头开始行索引
            List<Integer> rowHdColArea = rowHeadCfg.getRowHdColArea();  // 行头 开始列 和 结束列 的索引

            if (rowHdStartRowI == null || CollectionUtils.isEmpty(rowHdColArea)) return;

            Integer rValProcessMtd = rowHeadCfg.getrValProcessMtd();   // 行头原值处理方式
            Map<String, String> rowHdConvertMapper = rowHeadCfg.getRowHdConvertMapper(); // 行头转换映射
            Boolean enablePrefix = rowHeadCfg.getEnablePrefix();    // 是否启用前缀

            Map<String, Integer> rowHeadIdxMap = getRowHeadIndex(sheet, rowHdStartRowI, rowHdColArea, rValProcessMtd,
                    rowHdConvertMapper, enablePrefix);
            res.putAll(rowHeadIdxMap);
        });

        return res;
    }

    /**
     * 列头配置处理方法
     * @param colHeadCfgList
     * @param sheet
     * @return
     */
    private Map<String, Integer> processColHeadCfg(List<ColHeadCfg> colHeadCfgList, Sheet sheet) {
        if (CollectionUtils.isEmpty(colHeadCfgList)) return null;
        HashMap<String, Integer> res = new HashMap<>();

        colHeadCfgList.forEach(colHeadCfg -> {
            Integer colHdStartColI = colHeadCfg.getColHdStartColI();    // 列头开始列索引
            List<Integer> colHdRowArea = colHeadCfg.getColHdRowArea();  // 列头 开始行 和 结束行 的索引

            if (colHdStartColI == null || CollectionUtils.isEmpty(colHdRowArea)) return;

            Integer cValProcessMtd = colHeadCfg.getcValProcessMtd();   // 列头原值处理方式
            Map<String, String> colHdConvertMapper = colHeadCfg.getColHdConvertMapper(); // 列头转换映射
            Boolean enablePrefix = colHeadCfg.getEnablePrefix();    // 是否启用前缀

            Map<String, Integer> colHeadIdxMap = getColHeadIndex(sheet, colHdStartColI, colHdRowArea, cValProcessMtd,
                    colHdConvertMapper, enablePrefix);
            res.putAll(colHeadIdxMap);
        });

        return res;
    }

    /**
     * 从给定的 行头 区域中得到索引映射
     * 返回映射，行头中的值为key，值所在 列 的索引为value
     * @param sheet             工作表
     * @param rowHdStartRowI    行头开始的行索引
     * @param rowHdColArea      行头区域，开始列和结束列索引
     * @param processValMethod      对行头原值的处理方式，0: 不处理, 1: 从括号中取值
     * @param rowHdConvertMapper      对处理后的值进行映射
     * @param enablePrefix      是否启用前缀
     */
    private Map<String, Integer> getRowHeadIndex(Sheet sheet, Integer rowHdStartRowI, List<Integer> rowHdColArea,
                                                 Integer processValMethod, Map<String, String> rowHdConvertMapper, Boolean enablePrefix) {
        if (rowHdStartRowI == null) {
            throw new RuntimeException("行头起始行索引为空");
        }
        checkHdArea(rowHdColArea, "rowHdColArea");

        Map<String, Integer> rowHeadIndex = new HashMap<>();

        Row startRow = getRow(sheet, rowHdStartRowI);    // 起始行

        for (int i = 0; i < rowHdColArea.size(); i += 2) {
            Integer startCol = rowHdColArea.get(i); // 行头区域，开始列索引
            Integer endCol = rowHdColArea.get(i + 1);   // 行头区域，结束列索引

            for (int idx = startCol; idx <= endCol; idx++) {
                Cell cell = getCell(startRow, idx);
                // 添加索引映射
                addIndexMap(cell, processValMethod, rowHeadIndex, idx, rowHdStartRowI, sheet.getSheetName(),
                        rowHdConvertMapper, enablePrefix);
            }
        }

        return rowHeadIndex;
    }

    /**
     *  从给定的 列头 区域中得到索引映射
     *  返回映射，列头中的值为key，值所在 行 的索引为value
     * @param sheet                 工作表
     * @param colHdStartColI        列头开始列索引
     * @param colHdRowArea          列头区域，开始行和结束行索引
     * @param processValMethod      对行头原值的处理方式，0: 不处理, 1: 从括号中取值
     * @param colHdConvertMapper      对处理后的值进行的映射
     * @param enablePrefix      是否启用前缀
     * @return
     */
    private Map<String, Integer> getColHeadIndex(Sheet sheet, Integer colHdStartColI, List<Integer> colHdRowArea,
                                                 Integer processValMethod, Map<String, String> colHdConvertMapper, Boolean enablePrefix) {
        if (colHdStartColI == null) {
            throw new RuntimeException("列头起始列索引为空");
        }
        checkHdArea(colHdRowArea, "colHdRowArea");

        Map<String, Integer> colHeadIndex = new HashMap<>();

        for (int i = 0; i < colHdRowArea.size(); i += 2) {
            Integer startRow = colHdRowArea.get(i); // 列头区域，开始行索引
            Integer endRow = colHdRowArea.get(i + 1);   // 列头区域，结束行索引

            for (int idx = startRow; idx <= endRow; idx++) {
                Row row = getRow(sheet, idx);
                Cell cell = getCell(row, colHdStartColI);   // 对应当前列
                // 添加索引映射
                addIndexMap(cell, processValMethod, colHeadIndex, idx, colHdStartColI, sheet.getSheetName(),
                        colHdConvertMapper, enablePrefix);
            }
        }

        return colHeadIndex;
    }

    /**
     * 校验头区间
     * @param headArea
     * @param prefix
     */
    private void checkHdArea(List<Integer> headArea, String prefix) {
        if (CollectionUtils.isEmpty(headArea)) {
            throw new RuntimeException(prefix + " 为空");
        }
        assertEvenSize(headArea, prefix);
    }

    private void assertEvenSize(List<Integer> areaList, String label) {
        if (areaList == null || areaList.size() % 2 != 0) {
            throw new IllegalArgumentException(label + " 必须为偶数长度，表示成对的起止行号");
        }
    }

    /**
     * 添加索引映射
     * @param cell
     * @param processValMethod
     * @param headIndexMap
     * @param index
     */
    private void addIndexMap(Cell cell, Integer processValMethod, Map<String, Integer> headIndexMap, Integer index,
                             Integer headIdx, String sheetName, Map<String, String> hdConvertMapper, Boolean enablePrefix) {
        if (cell.getCellType() != CellType.STRING) {
            String letter = ExcelUtil.getExcelColumnLetter(cell.getColumnIndex());
            log.warn("工作表【{}】单元格【{}{}】不是文本", sheetName, letter, cell.getRowIndex() + 1);
            return;
        }

        String cellVal = cell.getStringCellValue().trim();
        if (cellVal.isEmpty()) return;

        // 处理，得到处理后的值
        String after = processValue(cellVal, processValMethod);
        if (StringUtils.isEmpty(after)) return;

        // 如果有映射，则用after映射，用得到的值作为key
        if (!MapUtils.isEmpty(hdConvertMapper)) {
            after = hdConvertMapper.get(after);
            if (StringUtils.isEmpty(after)) return;
        }

        // 是否启用前缀
        String key = enablePrefix == null || !enablePrefix
                ? after : headIdx + ":" + after;
        if (headIndexMap.containsKey(key)) return;
        headIndexMap.put(key, index);
    }

    /**
     * 获取行，不存在则新建
     * @param sheet     工作表
     * @param rowIndex  行索引
     * @return
     */
    private Row getRow(Sheet sheet, Integer rowIndex) {
        Row row = sheet.getRow(rowIndex);
        if (row == null) {
            row = sheet.createRow(rowIndex);
        }
        return row;
    }

    /**
     * 获取单元格，不存在则新建
     * @param row       行
     * @param colIndex  列索引
     * @return
     */
    private Cell getCell(Row row, Integer colIndex) {
        Cell cell = row.getCell(colIndex);
        if (cell == null) {
            cell = row.createCell(colIndex);
        }
        return cell;
    }

    /**
     * 处理方法分支
     * @param value
     * @param method
     * @return
     */
    private String processValue(String value, Integer method) {
        if (StringUtils.isEmpty(value)) return null;
        if (method == null) method = 0;
        switch (method) {
            case 1:
                return process1(value);
            case 2:
                return process2(value);
            case 3:
                return process3(value);
            default:
                return value;
        }
    }

    /**
     * 处理方式1，查找括号中的值
     * @param value
     * @return
     */
    private String process1(String value) {
        Matcher matcher = PATTERN1.matcher(value);
        if (matcher.find()) {
            return matcher.group(1);
        }
        log.warn("未匹配到括号内的内容，原始值: {}", value);
        return null;
    }

    /**
     * 处理方式2，查找-之前的的值
     * @param value
     * @return
     */
    private String process2(String value) {
//        if (!value.contains("-")) return null;
//        Matcher matcher = PATTERN2.matcher(value);
//        if (matcher.find()) {
//            return matcher.group(0);
//        }
        int i = value.indexOf("-");
        if (i == -1) {
            log.warn("未匹配到-之前的内容，原始值: {}", value);
            return null;
        }else{
           return value.substring(0, i);
        }
    }

    /**
     * 处理方式3，查找【】内的值
     * @param value
     * @return
     */
    private String process3(String value) {
        if (!value.contains("【") || !value.contains("】")) return null;
        Matcher matcher = PATTERN3.matcher(value);
        if (matcher.find()) {
            return matcher.group(1);
        }
        log.warn("未匹配到【】内的内容，原始值: {}", value);
        return null;
    }

    /**
     * 从数据模板中提取行列头对应的数据
     * @param filepath      数据模板文件
     * @param readDataCfg   列头映射数据集相关的配置
     * @return
     * @throws IOException
     */
    public Map<String, Map<String, String>> getRowColHeadMapData(String filepath, RowColHeadDataMapperCfg readDataCfg) throws IOException {
        return getRowColHeadMapData(new File(filepath), readDataCfg);
    }

    /**
     * 从数据模板中提取行列头对应的数据
     * @param path      数据模板文件路径对象
     * @param readDataCfg   列头映射数据集相关的配置
     * @return
     * @throws IOException
     */
    public Map<String, Map<String, String>> getRowColHeadMapData(Path path, RowColHeadDataMapperCfg readDataCfg) throws IOException {
        return getRowColHeadMapData(path.toFile(), readDataCfg);
    }

    /**
     * 从数据模板中提取行列头对应的数据
     * @param file      数据模板文件
     * @param readDataCfg   列头映射数据集相关的配置
     * @return
     * @throws IOException
     */
    public Map<String, Map<String, String>> getRowColHeadMapData(File file, RowColHeadDataMapperCfg readDataCfg) throws IOException {
        Map<String, Map<String, String>> result = new HashMap<>();

        try (InputStream is = new FileInputStream(file);
             Workbook workbook = WorkbookFactory.create(is)) {

            Integer sheetIndex = readDataCfg.getSheetIndex();
            if (sheetIndex < 0 || sheetIndex >= workbook.getNumberOfSheets()) {
                throw new IllegalArgumentException("无效的 sheetIndex: " + sheetIndex);
            }

            Sheet sheet = workbook.getSheetAt(sheetIndex);

            List<RowHeadCfg> rowHeadCfgList = readDataCfg.getRowHeadCfgList();
            List<ColHeadCfg> colHeadCfgList = readDataCfg.getColHeadCfgList();

            for (ColHeadCfg colHeadCfg : colHeadCfgList) {
                List<Integer> colIndexList = colHeadCfg.getColHdStartColIndexList();
                if (CollectionUtils.isEmpty(colIndexList)) {
                    throw new RuntimeException("列头开始列索引集合为空");
                }
                Integer cValProcessMtd = colHeadCfg.getcValProcessMtd();
                List<Integer> colHdRowArea = colHeadCfg.getColHdRowArea();
                assertEvenSize(colHdRowArea, "colHdRowArea");
                String connector = colHeadCfg.getConnector();

                for (int i = 0; i < colHdRowArea.size(); i += 2) {
                    for (int rowIndex = colHdRowArea.get(i); rowIndex <= colHdRowArea.get(i + 1); rowIndex++) {
                        Row dataRow = sheet.getRow(rowIndex);
                        String rowKey = getRowKey(dataRow, colIndexList, cValProcessMtd, connector);
                        if (StringUtils.isEmpty(rowKey)) continue;
                        Map<String, String> innerMap = result.computeIfAbsent(rowKey, k -> new HashMap<>());

                        for (RowHeadCfg rowHeadCfg : rowHeadCfgList) {
                            Integer rowHdRowIndex = Objects.requireNonNull(rowHeadCfg.getRowHdStartRowI(), "行头开始行为null");
                            List<Integer> rowHdColArea = rowHeadCfg.getRowHdColArea();
                            assertEvenSize(rowHdColArea, "rowHdColArea");
                            Integer offset = rowHeadCfg.getOffset() == null ? 0 : rowHeadCfg.getOffset();
                            Integer rValProcessMtd = rowHeadCfg.getrValProcessMtd();
                            Map<String, String> rowHdConvertMapper = rowHeadCfg.getRowHdConvertMapper();

                            Row headerRow = sheet.getRow(rowHdRowIndex);
                            for (int j = 0; j < rowHdColArea.size(); j += 2) {
                                for (int colIndex = rowHdColArea.get(j); colIndex <= rowHdColArea.get(j + 1); colIndex++) {
                                    String colVal = getCellVal(getCell(headerRow, colIndex));
                                    String colKey = processValue(colVal, rValProcessMtd);
                                    if (StringUtils.isEmpty(colKey)) continue;

                                    String falRowKey = getMapColKey(colKey, rowHdConvertMapper);
                                    Cell valueCell = getCell(dataRow, colIndex + offset);
                                    innerMap.put(falRowKey, getCellVal(valueCell));
                                }
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    /**
     * 获取一行对应的标识
     * @param dataRow
     * @param colIndexList
     * @param cValProcessMtd
     * @param connector
     * @return
     */
    private String getRowKey(Row dataRow, List<Integer> colIndexList, Integer cValProcessMtd, String connector) {
        List<String> colValList = colIndexList.stream().map(colIndex -> {
                    String colVal = getCellVal(getCell(dataRow, colIndex));
                    return processValue(colVal, cValProcessMtd);
                }).filter(StringUtils::isNotEmpty)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(colValList)) return null;
        return String.join(connector, colValList);
    }

    /**
     * 获取单元格的值
     * @param cell
     * @return
     */
    private String getCellVal(Cell cell) {
        if (cell == null) {
            log.warn("cell为null");
            return null;
        }
        String value;
        switch (cell.getCellType()) {
            case STRING:
                value = cell.getStringCellValue();
                break;
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    // 日期类型
                    Date date = cell.getDateCellValue();
                    value = new SimpleDateFormat("yyyy-MM-dd").format(date);
                } else {
                    // 数值类型
                    value = String.valueOf(cell.getNumericCellValue());
                }
                break;
            case BOOLEAN:
                value = String.valueOf(cell.getBooleanCellValue());
                break;
            case FORMULA:
                // 可以根据需要获取公式结果
                value = String.valueOf(cell.getNumericCellValue()); // 或用 evaluateFormulaCell
                break;
            case BLANK:
                value = "";
                break;
            default:
                value = "";
        }
        return value;
    }

    /**
     * 获取转换后的列标识
     * @param colKey                列标识
     * @param rowHdConvertMapper    行头转换映射，将列标识转换为对应key
     * @return
     */
    private String getMapColKey(String colKey, Map<String, String> rowHdConvertMapper) {
        if (MapUtils.isEmpty(rowHdConvertMapper)) return colKey;
        if (!rowHdConvertMapper.containsKey(colKey)) {
            log.warn("行头转换映射【rowHdConvertMapper】中没有对应的键: 【{}】, 则此键不会转换", colKey);
            return colKey;
        }
        return rowHdConvertMapper.get(colKey);
    }
}
