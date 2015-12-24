/**
 *
 */
package com.programmeryuan.PKUCarrier.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期提供者
 *
 * @author Michael
 */
public class DateProvider {
    public static String format_date = "yyyy-MM-dd";
    public static String format_datetime = "yyyy-MM-dd HH:mm:ss";
    public static String format_datetime_nosecond = "yyyy-MM-dd HH:mm";
    public static String format_clock = "HH:mm:ss:SSS";
    public static String format_time = "HH:mm:ss";
    public static String format_raw = "yyyyMMddHHmmss";
    public static String format_timezone = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    /**
     * 获取用户友好的日期，如“1分钟前”
     *
     * @param t 时间（毫秒）
     * @return 日期
     */
    public static String getReadableDate(long t) {
        try {
            long cur = System.currentTimeMillis();
            long offset = cur - t;
            if (offset < 60 * 1000 * 2) {// 1分钟内
//                return (int) (offset / 1000) + "秒前";
                return "刚刚";
            } else if (offset < 60 * 60 * 1000) {// 1小时内
                return (int) (offset / 60 / 1000) + "分钟前";
            } else if (offset < 24 * 60 * 60 * 1000) {// 1天内
                return (int) (offset / 60 / 60 / 1000) + "小时前";
            } else if (offset < 7 * 24 * 60 * 60 * 1000) {// 7天内
                return (int) (offset / 24 / 60 / 60 / 1000) + "天前";
            } else {
                return getDate(format_date, t);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取今年
     *
     * @return 今年
     */
    public static int getThisYear() {
        int this_year = Calendar.getInstance().get(Calendar.YEAR);
        return this_year;
    }

    /**
     * 获取本月
     *
     * @return 本月
     */
    public static int getThisMonth() {
        int this_month = Calendar.getInstance().get(Calendar.MONTH) + 1;
        return this_month;
    }

    /**
     * 获取今天（在本月中第几天）
     *
     * @return 今天
     */
    public static int getToday() {
        int this_day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        return this_day;
    }

    /**
     * 获取用户友好的日期，如“1分钟前”
     *
     * @param s 时间，格式必须是{@link DateProvider#format_datetime}
     * @return 日期
     */
    public static String getReadableDate(String s) {
        if(s == null || s.isEmpty()) return "";
        long l = 0;
        try {
            l = Long.parseLong(s);
        } catch (NumberFormatException e) {
            SimpleDateFormat df = new SimpleDateFormat(format_datetime);
            Date date = null;
            try {
                date = df.parse(s);
                l = date.getTime();
            } catch (ParseException e2) {
                df = new SimpleDateFormat(format_date);

                try {
                    date = df.parse(s);
                    l = date.getTime();
                } catch (ParseException e1) {
                    e1.printStackTrace();
                    return "";
                }

            }
        }

        return getReadableDate(l);
    }

    /**
     * @param format 日期格式
     * @param l      long型日期
     * @return 日期字符串
     */
    public static String getDate(String format, long l) {
        SimpleDateFormat df = new SimpleDateFormat(format);
        Date date = new Date(l);
        return df.format(date);
    }

    /**
     * @param format 日期格式
     * @param s      {@link java.lang.String }型日期
     * @return 日期字符串
     */
    public static long getDate(String format, String s) {
        SimpleDateFormat df = new SimpleDateFormat(format);
        Date date = null;
        try {
            date = df.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.getTime();
    }

    /**
     * 获得计时器格式时间
     *
     * @param l    时间，毫秒
     * @param tick 是否显示分和秒之间的“：”
     * @return 计时器格式时间
     */
    public static String getClockLikeTime(long l, boolean tick) {
        int hour = (int) (l / (60 * 60 * 1000));
        int min = (int) ((l - hour * 60 * 60 * 1000) / (60 * 1000));
        int sec = (int) ((l - hour * 60 * 60 * 1000 - min * 60 * 1000) / (1000));
        int sec2 = (int) ((l - hour * 60 * 60 * 1000 - min * 60 * 1000 - sec * 1000) / (100));
        String min_s = (min < 10 ? "0" : "") + min;
        String sec_s = (sec < 10 ? "0" : "") + sec;
        return hour + ":" + min_s + (tick ? ":" : " ") + sec_s + "." + sec2;
    }

    /**
     * 获得计时器格式时间
     *
     * @param l 时间，毫秒
     * @return 计时器格式时间
     */
    public static String getClockLikeTime(long l) {
        return getClockLikeTime(l, true);
    }

    /**
     * 获得计时器格式时间
     *
     * @param s 时间
     * @return 计时器格式时间
     */
    public static long getClockLikeTime(String s) {
        s = s.replace("\\.", ":");
        String[] data = s.split(":", -1);
        int hour = Integer.parseInt(data[0]);

        int min = Integer.parseInt(data[1]);
        int sec = Integer.parseInt(data[2]);
//        int sec2 = Integer.parseInt(data[3]);
        return hour * 60 * 60 * 1000 + min * 60 * 1000 + sec * 1000;
    }
}
