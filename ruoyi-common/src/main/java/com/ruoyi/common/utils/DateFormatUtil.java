package com.ruoyi.common.utils;

import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * 日期格式化工具类
 */
public class DateFormatUtil {
    /**
     * 将日期字符串格式化为中文格式。
     *
     * @param dateStr 日期字符串，支持格式 yyyy、yyyy-MM、yyyy-MM-dd
     * @return 中文格式的日期，如 "2025年"、"2025年05月"、"2025年05月22日"
     */
    public static String formatDateToChinese(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return "";
        }

        try {
            if (dateStr.matches("^\\d{4}$")) {
                return dateStr + "年";
            } else if (dateStr.matches("^\\d{4}-\\d{2}$")) {
                String[] parts = dateStr.split("-");
                return parts[0] + "年" + parts[1] + "月";
            } else if (dateStr.matches("^\\d{4}-\\d{2}-\\d{2}$")) {
                String[] parts = dateStr.split("-");
                return parts[0] + "年" + parts[1] + "月" + parts[2] + "日";
            } else {
                return dateStr; // 非预期格式，原样返回
            }
        } catch (Exception e) {
            // 出错时返回原始字符串
            return dateStr;
        }
    }

    /**
     * 将日期字符串格式化为指定的中文形式
     *
     * @param dateStr       原始日期字符串，可为 yyyy、yyyy-MM 或 yyyy-MM-dd
     * @param targetPattern 目标格式（如 "yyyy年MM月", "yyyy-MM", "yyyy年"）
     * @return 格式化后的中文日期字符串
     */
    public static String formatToChinese(String dateStr, String targetPattern) {
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern(targetPattern, Locale.CHINA);

        if (dateStr.matches("\\d{4}")) {
            // yyyy 格式，用 Year 解析，补全为 1 月 1 日
            Year year = Year.parse(dateStr, DateTimeFormatter.ofPattern("yyyy"));
            return year.atMonth(1).atDay(1).format(outputFormatter);
        } else if (dateStr.matches("\\d{4}-\\d{2}")) {
            // yyyy-MM 格式
            YearMonth ym = YearMonth.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM"));
            return ym.atDay(1).format(outputFormatter);
        } else if (dateStr.matches("\\d{4}-\\d{2}-\\d{2}")) {
            // yyyy-MM-dd 格式
            LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            return date.format(outputFormatter);
        } else {
            throw new IllegalArgumentException("不支持的日期格式: " + dateStr);
        }
    }

    public static String formatToChinese(YearMonth date, String targetPattern){
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern(targetPattern, Locale.CHINA);
        return date.atDay(1).format(outputFormatter);
    }

    /**
     * 将 yyyy-MM 格式的日期字符串转换为中文“1月到X月”格式，例如："2025-04" -> "2025年1-4月"
     *
     * @param yearMonthStr 输入字符串，格式为 yyyy-MM
     * @return 中文格式的字符串，例如："2025年1-4月"
     */
    public static String formatRangeFromStart(String yearMonthStr) {
        if (!yearMonthStr.matches("\\d{4}-\\d{2}")) {
            throw new IllegalArgumentException("输入格式必须为 yyyy-MM，例如 2025-04");
        }

        String[] parts = yearMonthStr.split("-");
        String year = parts[0];
        int month = Integer.parseInt(parts[1]);

        return String.format("%s年1-%d月", year, month);
    }

    /**
     * 将 yyyy-MM 格式的日期字符串转换为yyyy-MM-dd格式，日期为当月最后一天
     * 如 2025-04 -> 2025-04-30
     * @param yearMonthStr
     * @return
     */
    public static String formatToEnd(String yearMonthStr) {
        if (!yearMonthStr.matches("\\d{4}-\\d{2}")) {
            throw new IllegalArgumentException("输入格式必须为 yyyy-MM，例如 2025-04");
        }

        YearMonth ym = YearMonth.parse(yearMonthStr, DateTimeFormatter.ofPattern("yyyy-MM"));
        // 定义解析和输出的格式化器
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM");
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // 解析为 YearMonth 对象
        YearMonth yearMonth = YearMonth.parse(yearMonthStr, inputFormatter);

        // 获取该月最后一天（LocalDate），并格式化
        return yearMonth.atEndOfMonth().format(outputFormatter);
    }
}
