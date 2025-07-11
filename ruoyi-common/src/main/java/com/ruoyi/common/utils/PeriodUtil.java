package com.ruoyi.common.utils;

import java.time.Month;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

/**
 * 期间工具类
 */
public class PeriodUtil {
    private static final String PERIOD_REGEX = "\\d{4}-\\d{2}";
    private static final DateTimeFormatter YYYY_MM_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    /**
     * 传入期间(yyyy-MM)，返回上一期
     * @param period 当前期间，格式为 yyyy-MM
     * @return 上一期，格式为 yyyy-MM
     */
    public static String getPreviousPeriod(String period) {
        check(period);
        YearMonth current = YearMonth.parse(period, YYYY_MM_FORMATTER);
        return getPreviousPeriod(current);
    }

    /**
     * 传入期间，返回上一期
     * @param period 当前期间，格式为 yyyy-MM
     * @return 上一期，格式为 yyyy-MM
     */
    public static String getPreviousPeriod(YearMonth period) {
        YearMonth previous = period.minusMonths(1);
        return previous.format(YYYY_MM_FORMATTER);
    }

    /**
     * 传入期间(yyyy-MM)，返回首期（同年的1月）
     * @param period 当前期间，格式为 yyyy-MM
     * @return 首期，格式为 yyyy-MM
     */
    public static String firstPeriod(String period) {
        YearMonth current = getYearMonth(period);
        // 如果已经是1月，直接返回原值
        if (current.getMonthValue() == 1) {
            return period;
        }
        return withMonth(current, 1);
    }

    /**
     * 传入期间(yyyy-MM)，返回首期（同年的1月）
     * @param period 当前期间，格式为 yyyy-MM
     * @return 首期，格式为 yyyy-MM
     */
    public static String firstPeriod(YearMonth period) {
        // 如果已经是1月，直接返回原值
        if (period.getMonthValue() == 1) {
            return toPeriodStr(period);
        }
        return withMonth(period, 1);
    }

    /**
     * 传入期间(yyyy-MM)，返回末期（同年的12月）
     * @param period 当前期间，格式为 yyyy-MM
     * @return 首期，格式为 yyyy-MM
     */
    public static String endPeriod(String period) {
        YearMonth current = getYearMonth(period);
        // 如果已经是12月，直接返回原值
        if (current.getMonthValue() == 12) {
            return period;
        }
        return withMonth(current, 12);
    }

    /**
     * 传入期间(yyyy-MM)，返回末期（同年的12月）
     * @param period 当前期间，格式为 yyyy-MM
     * @return 首期，格式为 yyyy-MM
     */
    public static String endPeriod(YearMonth period) {
        // 如果已经是12月，直接返回原值
        if (period.getMonthValue() == 12) {
            return toPeriodStr(period);
        }
        return withMonth(period, 12);
    }

    /**
     * 改变当年期间为指定月份
     * @param current
     * @param month
     * @return
     */
    public static String withMonth(YearMonth current, int month) {
        YearMonth yearMonth = current.withMonth(month);
        return toPeriodStr(yearMonth);
    }

    /**
     * 上年同期
     * @param period
     * @return
     */
    public static String lastYearPeriod(String period) {
        YearMonth current = getYearMonth(period);
        YearMonth lastYear = current.minusYears(1);
        return lastYear.format(YYYY_MM_FORMATTER);
    }

    /**
     * 上年同期
     * @param period
     * @return
     */
    public static String lastYearPeriod(YearMonth period) {
        return toPeriodStr(period.minusYears(1));
    }

    /**
     * 获取期间年
     * @param period
     * @return
     */
    public static int getPeriodYear(String period) {
        return getYearMonth(period).getYear();
    }

    /**
     * 获取期间月的值
     * @param period
     * @return
     */
    public static int getPeriodMonthVal(String period) {
        return getPeriodMonth(period).getValue();
    }

    /**
     * 获取期间月，返回月份对象
     * @param period
     * @return
     */
    public static Month getPeriodMonth(String period) {
        return getYearMonth(period).getMonth();
    }

    /**
     * 校验，日期格式是否为yyyy-MM
     * @param period
     */
    public static void check(String period) {
        if (!period.matches(PERIOD_REGEX)) {
            throw new IllegalArgumentException("期间格式必须为 yyyy-MM");
        }
    }

    /**
     * 返回期间的YearMonth对象
     * @param period
     * @return
     */
    public static YearMonth getYearMonth(String period) {
        check(period);
        return YearMonth.parse(period, YYYY_MM_FORMATTER);
    }

    /**
     * 将YearMonth转换为字符串
     * 格式为: yyyy-MM
     * @param yearMonth
     * @return
     */
    public static String toPeriodStr(YearMonth yearMonth) {
        return yearMonth.format(YYYY_MM_FORMATTER);
    }
}
