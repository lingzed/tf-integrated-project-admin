package com.ruoyi.common.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.*;
import java.util.*;

public class ExcelUtils {

    /**
     * 读取Excel文件内容（自动识别xls/xlsx格式）
     * @param filePath 文件路径
     * @return 包含所有Sheet数据的列表
     */
    public static List<SheetData> readExcel(String filePath) throws IOException {
        File file = new File(filePath);
        return readExcel(file);
    }

    /**
     * 读取Excel文件内容（自动识别xls/xlsx格式）
     * @param io 输入流
     * @return
     * @throws IOException
     */
    public static List<SheetData> readExcel(InputStream io) throws IOException {
        List<SheetData> sheets = new ArrayList<>();

        Workbook workbook = WorkbookFactory.create(io);

        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            Sheet sheet = workbook.getSheetAt(i);
            SheetData sheetData = processSheet(sheet);
            sheets.add(sheetData);
        }

        return sheets;
    }

    /**
     * 读取Excel文件内容（自动识别xls/xlsx格式）
     * @param file 文件
     * @return 包含所有Sheet数据的列表
     */
    public static List<SheetData> readExcel(File file) throws IOException {
        List<SheetData> sheets = new ArrayList<>();

        try (InputStream inp = new FileInputStream(file);
             Workbook workbook = WorkbookFactory.create(inp)) {

            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                Sheet sheet = workbook.getSheetAt(i);
                SheetData sheetData = processSheet(sheet);
                sheets.add(sheetData);
            }
        }
        return sheets;
    }


    private static SheetData processSheet(Sheet sheet) {
        SheetData sheetData = new SheetData();
        sheetData.setSheetName(sheet.getSheetName());
        List<List<Object>> rows = new ArrayList<>();

        int lastRowNum = sheet.getLastRowNum();
        for (int rowIdx = 0; rowIdx <= lastRowNum; rowIdx++) {
            Row row = sheet.getRow(rowIdx);
            rows.add(processRow(row));
        }

        sheetData.setRows(rows);
        return sheetData;
    }

    private static List<Object> processRow(Row row) {
        List<Object> rowData = new ArrayList<>();
        if (row == null) return rowData;

        int lastCellNum = row.getLastCellNum();
        for (int cellIdx = 0; cellIdx < lastCellNum; cellIdx++) {
            Cell cell = row.getCell(cellIdx, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            rowData.add(getCellValue(cell));
        }
        return rowData;
    }

    /**
     * 写入数据到新Excel文件（根据扩展名自动选择xls/xlsx格式）
     * @param outputPath 输出文件路径
     * @param sheets 要写入的Sheet数据
     */
    public static void writeExcel(String outputPath, List<SheetData> sheets, Boolean onlyFirst) throws IOException {
        if (sheets == null || sheets.isEmpty()) {
            throw new IllegalArgumentException("必须包含至少一个Sheet");
        }

        Workbook workbook = createWorkbook(outputPath);
        int writeCount = onlyFirst ? 1 : sheets.size();

        for (int i = 0; i < writeCount; i++) {
            SheetData sheetData = sheets.get(i);
            Sheet sheet = workbook.createSheet(sheetData.getSheetName());
            List<List<Object>> rows = sheetData.getRows();

            for (int rowNum = 0; rowNum < rows.size(); rowNum++) {
                Row row = sheet.createRow(rowNum);
                List<Object> rowData = rows.get(rowNum);

                for (int colNum = 0; colNum < rowData.size(); colNum++) {
                    Object value = rowData.get(colNum);
                    Cell cell = row.createCell(colNum);
                    setCellValue(cell, value);
                }
            }
        }

        try (FileOutputStream fos = new FileOutputStream(outputPath)) {
            workbook.write(fos);
        }
        workbook.close();
    }

    // 根据文件扩展名创建Workbook
    private static Workbook createWorkbook(String filePath) {
        String extension = filePath.substring(filePath.lastIndexOf(".") + 1).toLowerCase();
        return extension.equals("xls") ? new HSSFWorkbook() : new XSSFWorkbook();
    }

    // 获取单元格值（处理公式）
    private static Object getCellValue(Cell cell) {
        CellType cellType = cell.getCellType();
        if (cellType == CellType.FORMULA) {
            return getFormulaCellValue(cell);
        }
        return getBasicCellValue(cell);
    }

    // 处理公式单元格
    private static Object getFormulaCellValue(Cell cell) {
        switch (cell.getCachedFormulaResultType()) {
            case NUMERIC:
                return cell.getNumericCellValue();
            case STRING:
                return cell.getStringCellValue();
            case BOOLEAN:
                return cell.getBooleanCellValue();
            case ERROR:
                return cell.getErrorCellValue();
            default:
                return "";
        }
    }

    // 处理普通单元格
    private static Object getBasicCellValue(Cell cell) {
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return DateUtil.isCellDateFormatted(cell) ?
                        cell.getDateCellValue() :
                        cell.getNumericCellValue();
            case BOOLEAN:
                return cell.getBooleanCellValue();
            case BLANK:
                return "";
            default:
                return "";
        }
    }

    // 设置单元格值
    private static void setCellValue(Cell cell, Object value) {
        if (value == null) return;

        if (value instanceof Number) {
            cell.setCellValue(((Number) value).doubleValue());
        } else if (value instanceof Date) {
            cell.setCellValue((Date) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else {
            cell.setCellValue(value.toString());
        }
    }

    // Sheet数据封装类
    public static class SheetData {
        private String sheetName;
        private List<List<Object>> rows;

        // Getter和Setter
        public String getSheetName() {
            return sheetName;
        }

        public void setSheetName(String sheetName) {
            this.sheetName = sheetName;
        }

        public List<List<Object>> getRows() {
            return rows;
        }

        public void setRows(List<List<Object>> rows) {
            this.rows = rows;
        }
    }

    // 使用示例
    public static void main(String[] args) {
        try {
            // 读取Excel文件
            List<SheetData> sheets = ExcelUtils.readExcel("input.xlsx");

            // 处理数据（示例：在第一个Sheet第一行添加新数据）
            if (!sheets.isEmpty()) {
                sheets.get(0).getRows().add(0, Arrays.asList("New Data", 12345, true));
            }

            // 写入新文件
            ExcelUtils.writeExcel("output.xlsx", sheets, true);

            System.out.println("Excel处理完成！");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 计算列的索引
     *
     * @param colStr
     * @return
     */
    public static int getColIndex(String colStr) {
        colStr = colStr.toUpperCase(); // 转大写，确保大小写一致
        int index = 0;
        for (int i = 0; i < colStr.length(); i++) {
            index = index * 26 + (colStr.charAt(i) - 'A' + 1);
        }
        return index - 1; // Excel 从1开始，程序中从0开始
    }

    /**
     * 列索引转列字母
     * @param columnIndex
     * @return
     */
    public static String getExcelColumnLetter(int columnIndex) {
        StringBuilder columnName = new StringBuilder();

        while (columnIndex >= 0) {
            int remainder = columnIndex % 26;
            columnName.insert(0, (char) ('A' + remainder));
            columnIndex = (columnIndex / 26) - 1;
        }

        return columnName.toString();
    }
}