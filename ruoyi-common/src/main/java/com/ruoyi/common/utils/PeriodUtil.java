package com.ruoyi.common.utils;

import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 期间工具类
 */
public class PeriodUtil {
    private static final String PERIOD_REGEX = "\\d{4}-\\d{2}";
    private static final DateTimeFormatter YYYY_MM_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    /**
     * 传入期间(yyyy-MM)，返回上一期
     *
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
     *
     * @param period 当前期间，格式为 yyyy-MM
     * @return 上一期，格式为 yyyy-MM
     */
    public static String getPreviousPeriod(YearMonth period) {
        YearMonth previous = period.minusMonths(1);
        return previous.format(YYYY_MM_FORMATTER);
    }

    /**
     * 传入期间(yyyy-MM)，返回首期（同年的1月）
     *
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
     *
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
     *
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
     *
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
     *
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
     *
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
     *
     * @param period
     * @return
     */
    public static String lastYearPeriod(YearMonth period) {
        return toPeriodStr(period.minusYears(1));
    }

    /**
     * 获取期间年
     *
     * @param period
     * @return
     */
    public static int getPeriodYear(String period) {
        return getYearMonth(period).getYear();
    }

    /**
     * 获取期间月的值
     *
     * @param period
     * @return
     */
    public static int getPeriodMonthVal(String period) {
        return getPeriodMonth(period).getValue();
    }

    /**
     * 获取期间月，返回月份对象
     *
     * @param period
     * @return
     */
    public static Month getPeriodMonth(String period) {
        return getYearMonth(period).getMonth();
    }

    /**
     * 校验，日期格式是否为yyyy-MM
     *
     * @param period
     */
    public static void check(String period) {
        if (!period.matches(PERIOD_REGEX)) {
            throw new IllegalArgumentException("期间格式必须为 yyyy-MM");
        }
    }

    /**
     * 返回期间的YearMonth对象
     *
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
     *
     * @param yearMonth
     * @return
     */
    public static String toPeriodStr(YearMonth yearMonth) {
        return yearMonth.format(YYYY_MM_FORMATTER);
    }

    public static List<String> firstToCurrRange(String end) {
        check(end);
        List<String> range = new ArrayList<>();
        String yearStr = end.substring(0, 4);   // 年
        String mStr = end.substring(5, 7);  // 月份
        if (mStr.equals("01")) {
            range.add(end);
            return range;
        }
        int endM = Integer.parseInt(mStr);
        for (int i = 1; i <= endM - 1; i++) {
            range.add(String.format("%s-%02d", yearStr, i));
        }
        range.add(end);
        return range;
    }

    /**
     * 转入开始期间和结束期间，返回这段范围内所有期间<br>
     * 如：2025-01，2025-03 -> [2025-01, 2025-02, 2025-03]<br>
     * 如：2024-11，2025-02 -> [2024-11, 2024-12, 2025-01, 2025-02]<br>
     * 如：2025-01，2025-01 -> [2025-01]<br>
     *
     * @param start
     * @param end
     * @return
     */
    public static List<String> firstToCurrRange(String start, String end) {
        check(start);
        check(end);
        int startYear = Integer.parseInt(start.substring(0, 4));    // 起始期间年
        int endYear = Integer.parseInt(end.substring(0, 4));        // 结束期间年
        int startMonth = Integer.parseInt(start.substring(5, 7));  // 起始期间月
        int endMonth = Integer.parseInt(end.substring(5, 7));  // 结束期间月
        boolean sameYear = startYear == endYear;
        if (endYear < startYear || (sameYear && endMonth < startMonth)) {
            throw new RuntimeException("end must be greater than start");
        }
        List<String> range = new ArrayList<>();
        // 同年同月
        if (start.equals(end)) {
            range.add(start);
            return range;
        }
        // 同年
        if (sameYear) {
            for (int i = startMonth; i <= endMonth; i++) {
                range.add(String.format("%d-%02d", startYear, i));
            }
            return range;
        }
        // 跨年
        for (int y = startYear; y <= endYear; y++) {
            int m = y > startYear ? 1 : startMonth;
            int md = y < endYear ? 12 : endMonth;
            for (; m <= md; m++) {
                range.add(String.format("%d-%02d", y, m));
            }
        }
        return range;
    }

    /**
     * 返回当前月份的第一天和最后一天，返回形式为数组，第一项为第一天，第二项为最后一天
     *
     * @param period
     * @return
     */
    public static String[] currMonthFirstAndEnd(String period) {
        check(period);
        String yearStr = period.substring(0, 4);   // 年
        String mStr = period.substring(5, 7);  // 月份
        int lastDay = lastDayOfMonth(Integer.parseInt(yearStr), Integer.parseInt(mStr));
        // 格式化为两位数
        String endDay = String.format("%02d", lastDay);
        return new String[]{period + "-01", period + "-" + endDay};
    }

    /**
     * 返回当前期间月最后一天的日期，格式为yyyy-MM-dd<br>
     * 如：2025-01 -> 2025-01-31
     *
     * @param period
     * @return
     */
    public static String lastDayOfPeriod(String period) {
        check(period);
        String yearStr = period.substring(0, 4);
        String mStr = period.substring(5, 7);
        int lastDay = lastDayOfMonth(Integer.parseInt(yearStr), Integer.parseInt(mStr));
        return String.format("%s-%02d", period, lastDay);
    }

    private static int lastDayOfMonth(int year, int month) {
        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                return 31;
            case 4:
            case 6:
            case 9:
            case 11:
                return 30;
            case 2:
                // 判断闰年
                if ((year % 400 == 0) || (year % 4 == 0 && year % 100 != 0)) {
                    return 29;
                } else {
                    return 28;
                }
            default:
                throw new IllegalArgumentException("非法月份: " + month);
        }
    }
}
