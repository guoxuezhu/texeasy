package com.texeasy.base.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 时间工具类
 *
 * @author wanglei 2015年8月1日 上午7:03:28
 */
public class TimeUtil {

    /**
     * yyyy-MM-dd HH:mm:ss
     */
    public static String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    /**
     * yyyy-MM-dd HH:mm
     */
    public static String DEFAULT_HOUR_MINUTE_FORMAT = "yyyy-MM-dd HH:mm";
    /**
     * MM月dd日 HH:mm
     */
    public static String HOUR_MINUTE_FORMAT = "MM月dd日  HH:mm";
    /**
     * HH:mm
     */
    public static String HOUR = "HH:mm";
    /**
     * yyyy-MM-dd
     */
    public static String DEFAULT_DAY_FORMAT = "yyyy-MM-dd";
    /**
     * yyyy-MM-dd
     */
    public static String DEFAULT_MONTH_FORMAT = "yyyy-MM";
    /**
     * yyyyMMddHHmmss
     */
    public static String DEFAULT_DATE_NO_SEPRATOR_FORMAT = "yyyyMMddHHmmss";
    /**
     * yyyyMMdd
     */
    public static final String DEFAULT_DAY_NO_SEPRATOR_FORMAT = "yyyyMMdd";
    /**
     * yyyyMMdd
     */
    public static final String DEFAULT_DAY_NO_SEPRATOR = "yyyy.MM.dd";
    /**
     * dd/MM/yyyy
     */
    public static final String DEFAULT_SLASH_FORMAT = "dd/MM/yyyy";
    /**
     * dd/MM/yyyy
     */
    public static final String DEFAULT_FORMAT = "yyyy/MM/dd/";
    /**
     * MMddHHmmss
     */
    public static final String DEFAULT_FORMAT_NO_YEAR = "MMddHHmmss";

    /**
     * 指定日期格式，转化时间字符串为Date对象
     *
     * @param pattern
     * @param dateString
     * @return
     */
    public static Date parseDate(String pattern, String dateString) {
        SimpleDateFormat df = new SimpleDateFormat(pattern);
        try {
            return df.parse(dateString);
        } catch (Exception e) {
            return new Date();
        }
    }

    /**
     * 指定日期格式，转化时间字符串为Date对象
     *
     * @return
     */
    public static Date parseDate(String dateString) {
        return parseDate(DEFAULT_DATE_FORMAT, dateString);
    }

    /**
     * 指定日期格式，转化Date对象为时间字符串
     *
     * @param pattern
     * @param date
     * @return
     */
    public static String parseString(String pattern, Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }

    /**
     * 指定日期格式，转化Date对象为时间字符串
     *
     * @param date
     * @return
     */
    public static String parseString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
        return sdf.format(date);
    }


    /**
     * 时间格式转换
     *
     * @return
     */
    public static String parseString(String dateStr) {
        Date date = parseDate(dateStr);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日HH时mm分");
        return sdf.format(date);
    }

    /**
     * Date转化为时间戳
     *
     * @return
     */
    public static Long dateToLong(String dateStr) {
        SimpleDateFormat format = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
        Date date = null;
        try {
            date = format.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.getTime();
    }

    /**
     * Date转化为时间戳
     *
     * @return
     */
    public static Long dateToLong(String pattern, String dateStr) {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        Date date = null;
        try {
            date = format.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.getTime();
    }

    /**
     * 时间戳转换成字符窜
     */
    public static String getDateToString(long time) {
        Date d = new Date(time);
        SimpleDateFormat sf = new SimpleDateFormat(HOUR_MINUTE_FORMAT);
        return sf.format(d);
    }

    /* 时间戳转换成字符窜 */
    public static String getDateToStringss(long time) {
        Date d = new Date(time);
        SimpleDateFormat sf = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
        return sf.format(d);
    }

    /**
     * 时间戳转换成字符窜
     *
     * @param pattern
     * @param time
     * @return
     */
    public static String getDateToString(String pattern, long time) {
        Date d = new Date(time);
        SimpleDateFormat sf = new SimpleDateFormat(pattern);
        return sf.format(d);
    }


    /**
     * 时间戳转换成字符窜
     */
    public static String getDateToStringOrder(long time) {
        Date d = new Date(time);
        SimpleDateFormat sf = new SimpleDateFormat(DEFAULT_HOUR_MINUTE_FORMAT);
        return sf.format(d);
    }

    /**
     * 时间戳转换成字符窜
     */
    public static String getDateToStringday(long time) {
        Date d = new Date(time);
        SimpleDateFormat sf = new SimpleDateFormat(DEFAULT_DAY_NO_SEPRATOR);
        return sf.format(d);
    }

    /**
     * 时间戳转换成字符窜
     */
    public static String getDateToStringHour(long time) {
        Date d = new Date(time);
        SimpleDateFormat sf = new SimpleDateFormat(HOUR);
        return sf.format(d);
    }
}
