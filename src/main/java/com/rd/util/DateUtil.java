package com.rd.util;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 时间相关工具
 * 尽量以long来计算，不要用{@code Calendar}
 *
 * @author Created by U-Demon on 2016年10月31日 下午7:20:32
 * @version 1.0.0
 */
public class DateUtil {
    /**
     * 时区
     **/
    static final ZoneId ZONE_ID = ZoneId.systemDefault();
    /**
     * 时差
     **/
    static final ZoneOffset ZONE_OFFSET = ZONE_ID.getRules().getOffset(Instant.now());
    static final String YEAR_STR = "天";
    static final String MONTH_STR = "月";
    static final String DAY_STR = "天";
    static final String HOUR_STR = "小时";
    static final String MINUTE_STR = "分";
    static final String SEC_STR = "秒";

    public static final long SECOND = 1000;
    public static final long MINUTE = 60 * SECOND;
    public static final long HOUR = 60 * MINUTE;
    public static final long DAY = 24 * HOUR;
    public static final long WEEK = 7 * DAY;

    static final String dateformatter = "yyyyMMdd";
    static final String dateformatter_month = "yyyy-MM";
    static final String dayformatter = "yyyy-MM-dd";
    static final String dayformatter_en = "MMM.dd'th'.yyyy";
    static final String timeformatter_minute = "HH:mm";
    static final String timeformatter_second = "HH:mm:ss";
    static final String datetimeformatter = "yyyy-MM-dd HH:mm:ss";
    static final String timeformatter_ms = "HH:mm:ss";
    static final String datetimeformatter_en = "HH:mm MMM.dd'th'.yyyy";
    static final String datetimeformatter_cron = "ss mm HH dd MM ? yyyy";
    static final String dateformat = "yyMMdd";

    static final ThreadLocal<SimpleDateFormat> threadDateFormat = new ThreadLocal<SimpleDateFormat>();
    static final ThreadLocal<SimpleDateFormat> threadDayFormat = new ThreadLocal<SimpleDateFormat>();
    static final ThreadLocal<SimpleDateFormat> threadDayFormatEn = new ThreadLocal<SimpleDateFormat>();
    static final ThreadLocal<SimpleDateFormat> threadTimeFormatMinute = new ThreadLocal<SimpleDateFormat>();
    static final ThreadLocal<SimpleDateFormat> threadTimeFormatSecond = new ThreadLocal<SimpleDateFormat>();
    static final ThreadLocal<SimpleDateFormat> threadDateTimeFormat = new ThreadLocal<SimpleDateFormat>();
    static final ThreadLocal<SimpleDateFormat> threadDateFormatMs = new ThreadLocal<SimpleDateFormat>();
    static final ThreadLocal<SimpleDateFormat> threadDateTimeFormatEn = new ThreadLocal<SimpleDateFormat>();
    static final ThreadLocal<SimpleDateFormat> threadDateTimeFormatCron = new ThreadLocal<SimpleDateFormat>();

    /**
     * 获取DateTimeformater实例
     */
    public static DateFormat getDateTimeFormatter() {
        SimpleDateFormat datetimeFormat = threadDateTimeFormat.get();
        if (datetimeFormat == null) {
            datetimeFormat = new SimpleDateFormat(datetimeformatter);
            threadDateTimeFormat.set(datetimeFormat);
        }
        return datetimeFormat;
    }

    /**
     * 获取DateTimeformater实例
     */
    public static DateFormat getDateTimeFormatterEn() {
        SimpleDateFormat datetimeFormat = threadDateTimeFormatEn.get();
        if (datetimeFormat == null) {
            datetimeFormat = new SimpleDateFormat(datetimeformatter_en, Locale.ENGLISH);
            threadDateTimeFormatEn.set(datetimeFormat);
        }
        return datetimeFormat;
    }

    /**
     * 获取Dateformater实例
     */
    public static DateFormat getDateFormatter() {
        SimpleDateFormat dateFormat = threadDateFormat.get();
        if (dateFormat == null) {
            dateFormat = new SimpleDateFormat(dateformatter);
            threadDateFormat.set(dateFormat);
        }
        return dateFormat;
    }

    /**
     * 获取Dateformater实例
     */
    public static DateFormat getDateFormat() {
        SimpleDateFormat dateFormat = threadDateFormat.get();
        if (dateFormat == null) {
            dateFormat = new SimpleDateFormat(dateformat);
            threadDateFormat.set(dateFormat);
        }
        return dateFormat;
    }

    /**
     * 获取Dateformater实例
     */
    public static DateFormat getMonthFormatter() {
        SimpleDateFormat dateFormat = threadDateFormat.get();
        if (dateFormat == null) {
            dateFormat = new SimpleDateFormat(dateformatter_month);
            threadDateFormat.set(dateFormat);
        }
        return dateFormat;
    }

    /**
     * 获取Dayformater实例
     */
    public static DateFormat getDayFormatter() {
        SimpleDateFormat dayFormat = threadDayFormat.get();
        if (dayFormat == null) {
            dayFormat = new SimpleDateFormat(dayformatter);
            threadDayFormat.set(dayFormat);
        }
        return dayFormat;
    }

    /**
     * 获取Dayformater实例
     */
    public static DateFormat getDayFormatterEn() {
        SimpleDateFormat dayFormat = threadDayFormatEn.get();
        if (dayFormat == null) {
            dayFormat = new SimpleDateFormat(dayformatter_en, Locale.ENGLISH);
            threadDayFormatEn.set(dayFormat);
        }
        return dayFormat;
    }

    /**
     * 获取TimeMinuteformater实例
     */
    public static DateFormat getTimeMinuteFormatter() {
        SimpleDateFormat timeMinuteFormat = threadTimeFormatMinute.get();
        if (timeMinuteFormat == null) {
            timeMinuteFormat = new SimpleDateFormat(timeformatter_minute);
            threadTimeFormatMinute.set(timeMinuteFormat);
        }
        return timeMinuteFormat;
    }

    /**
     * 获取TimeMinuteformater实例
     */
    public static DateFormat getTimeSecondFormatter() {
        SimpleDateFormat timeSecondFormat = threadTimeFormatSecond.get();
        if (timeSecondFormat == null) {
            timeSecondFormat = new SimpleDateFormat(timeformatter_second);
            threadTimeFormatSecond.set(timeSecondFormat);
        }
        return timeSecondFormat;
    }

    /**
     * 获取CronExpression时间表达式实例
     */
    public static DateFormat getCronExpressionFormatter() {
        SimpleDateFormat timeSecondFormat = threadDateTimeFormatCron.get();
        if (timeSecondFormat == null) {
            timeSecondFormat = new SimpleDateFormat(datetimeformatter_cron);
            threadDateTimeFormatCron.set(timeSecondFormat);
        }
        return timeSecondFormat;
    }

    /**
     * 格式化日期时间
     */
    public static String formatDateTime(Date date) {
        return getDateTimeFormatter().format(date);
    }

    /**
     * 格式化日期时间
     */
    public static String formatDateTime(long time) {
        return getDateTimeFormatter().format(time);
    }

    /**
     * 格式化日期时间
     */
    public static String formatDateTimeEn(Date date) {
        return getDateTimeFormatterEn().format(date);
    }

    /**
     * 格式化日期时间
     */
    public static String formatDateTimeEn(long time) {
        return getDateTimeFormatterEn().format(time);
    }

    /**
     * 格式化日期
     */
    public static String formatMonth(Date date) {
        return getMonthFormatter().format(date);
    }

    /**
     * 格式化日期
     */
    public static String formatMonth(long time) {
        return getMonthFormatter().format(time);
    }

    /**
     * 格式化日期
     */
    public static String formatDate(Date date) {
        return getDateFormatter().format(date);
    }

    /**
     * 格式化日期
     */
    public static String formatDate(long time) {
        return getDateFormatter().format(time);
    }

    /**
     * 格式化日期
     */
    public static String formatShortDate(long time) {
        return getDateFormat().format(time);
    }

    /**
     * 格式化日期
     */
    public static String formatDay(Date date) {
        return getDayFormatter().format(date);
    }

    /**
     * 格式化日期
     */
    public static String formatDayEn(Date date) {
        return getDayFormatterEn().format(date);
    }

    /**
     * 格式化日期
     */
    public static String formatDay(long time) {
        return getDayFormatter().format(time);
    }

    /**
     * 格式化日期
     */
    public static String formatDayEn(long time) {
        return getDayFormatterEn().format(time);
    }

    /**
     * 格式化时间
     */
    public static String formatTime(long time) {
        return getTimeMinuteFormatter().format(time);
    }

    /**
     * 格式化时间
     */
    public static String formatTime(Date date) {
        return getTimeMinuteFormatter().format(date);
    }

    /**
     * 格式化时间
     */
    public static String formatTimeSecond(long time) {
        return getTimeSecondFormatter().format(time);
    }

    /**
     * 格式化时间
     */
    public static String formatTimeSecond(Date date) {
        return getTimeSecondFormatter().format(date);
    }

    /**
     * 格式化CronExpression时间表达式
     */
    public static String formatCronExpression(Date date) {
        return getCronExpressionFormatter().format(date);
    }

    public static String formatTimeMS(long time) {
        SimpleDateFormat timeFormatMs = threadDateFormatMs.get();
        if (timeFormatMs == null) {
            timeFormatMs = new SimpleDateFormat(timeformatter_ms);
            threadDateFormatMs.set(timeFormatMs);
        }
        return timeFormatMs.format(time);
    }

    /**
     * 格式化日期时间(美式)
     */
    public static String formatDateMS(long time) {
        DateFormat df = DateFormat.getDateInstance(DateFormat.DEFAULT, new Locale("en-us", "US"));
        return df.format(new Date(time));
    }

    /**
     * 格式化日期时间(美式)
     */
    public static String formatDateMS(Date date) {
        DateFormat df = DateFormat.getDateInstance(DateFormat.DEFAULT, new Locale("en-us", "US"));
        return df.format(date);
    }

    /**
     * 格式化日期时间(美式)
     */
    public static String formatDateTimeMS(long time) {
        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, new Locale("en-us", "US"));
        return df.format(new Date(time));
    }

    /**
     * 格式化日期时间(美式)
     */
    public static String formatDateTimeMS(Date date) {
        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, new Locale("en-us", "US"));
        return df.format(date);
    }

    /**
     * 解析指定字符串表示的日期
     */
    public static Date parseDataTime(String source) {
        try {
            return getDateTimeFormatter().parse(source);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解析指定字符串表示的日期
     */
    public static Date parseDataDay(String source) {
        try {
            return getDayFormatter().parse(source);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解析指定字符串表示的日期
     */
    public static Date parseDataDayEn(String source) {
        try {
            return getDayFormatterEn().parse(source);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解析指定字符串表示的日期
     */
    public static Date parseDataTimeEn(String source) {
        try {
            return getDateTimeFormatterEn().parse(source);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解析指定字符串表示的日期
     */
    public static Date parseCronExpression(String source) {
        try {
            return getCronExpressionFormatter().parse(source);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据表达式计算开始时间,时间以毫秒形式标识
     */
    public static long nextTime(String exp, int period) {
        return nextDate(exp, period).getTime();
    }

    /**
     * 根据表达式计算开始时间，时间以Date格式标识
     *
     * @param exp    时间表达式<br />
     *               格式：年-月-日-星期-时-分
     * @param PERIOD 重复/间隔时间，以分为单位
     * @return
     */
    public static Date nextDate(String exp, int PERIOD) {
        Date time;
        try {
            String[] timer = exp.split("-");
            // Get the User provided Time
            Calendar userCal = Calendar.getInstance();
            // Get System Calendar Date
            Calendar sys = Calendar.getInstance();
            if ("*".equals(timer[0]))
                userCal.set(Calendar.YEAR, sys.get(java.util.Calendar.YEAR));
            else
                userCal.set(Calendar.YEAR, Integer.parseInt(timer[0]));
            if ("*".equals(timer[1]))
                userCal.set(Calendar.MONTH, sys.get(java.util.Calendar.MONTH));
            else
                userCal.set(Calendar.MONTH, Integer.parseInt(timer[1]));
            if ("*".equals(timer[2]))
                userCal.set(Calendar.DAY_OF_MONTH, sys.get(Calendar.DAY_OF_MONTH));
            else
                userCal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(timer[2]));
            if ("*".equals(timer[3]))
                userCal.set(Calendar.DAY_OF_WEEK, sys.get(java.util.Calendar.DAY_OF_WEEK));
            else
                userCal.set(Calendar.DAY_OF_WEEK, Integer.parseInt(timer[3]) + 1);
            if ("*".equals(timer[4]))
                userCal.set(Calendar.HOUR_OF_DAY, sys.get(java.util.Calendar.HOUR_OF_DAY));
            else
                userCal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timer[4]));
            if ("*".equals(timer[5]))
                userCal.set(Calendar.MINUTE, sys.get(java.util.Calendar.MINUTE));
            else
                userCal.set(Calendar.MINUTE, Integer.parseInt(timer[5]));
            userCal.set(Calendar.SECOND, 0);
            // Compare the two dates.
            while (userCal.getTime().getTime() < sys.getTime().getTime()) {
                // Time has passed. Next Occur Time
                userCal.add(Calendar.MINUTE, PERIOD);
            }
            // Set the time object
            time = userCal.getTime();
        } catch (Exception ex) {
            ex.printStackTrace();
            time = new Date();
        }
        return time;
    }

    /**
     * 获取当前月份值
     */
    public static int month() {
        return Calendar.getInstance().get(Calendar.MONTH) + 1;
    }

    /**
     * 某月第一天
     */
    public static String beginingOfMonth(int month) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.MONTH, month - 1);
        c.set(Calendar.DAY_OF_MONTH, c.getActualMinimum(Calendar.DAY_OF_MONTH));
        return getDateFormatter().format(c.getTime());
    }

    /**
     * 某月最后一天
     */
    public static String endOfMonth(int month) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.MONTH, month - 1);
        c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
        return getDateFormatter().format(c.getTime());
    }

    /**
     * 获得一天中24:00时间
     */
    public static Date endOfDay() {
        Calendar c = Calendar.getInstance(); // 得到当前日期和时间
        c.set(Calendar.HOUR_OF_DAY, 23); // 把当前时间小时变成０
        c.set(Calendar.MINUTE, 59); // 把当前时间分钟变成０
        c.set(Calendar.SECOND, 59); // 把当前时间秒数变成０
        c.set(Calendar.MILLISECOND, 999); // 把当前时间毫秒变成０
        return c.getTime(); // 创建当天的0时0分0秒一个date对象
    }

    /**
     * 获取一天的0点
     */
    public static Date startOfDay() {
        Calendar c = Calendar.getInstance(); // 得到当前日期和时间
        c.set(Calendar.HOUR_OF_DAY, 0); // 把当前时间小时变成０
        c.set(Calendar.MINUTE, 0); // 把当前时间分钟变成０
        c.set(Calendar.SECOND, 0); // 把当前时间秒数变成０
        c.set(Calendar.MILLISECOND, 0); // 把当前时间毫秒变成０
        return c.getTime(); // 创建当天的0时0分0秒一个date对象
    }

    /**
     * 获取一天的0点 FIXME  非线程安全
     */
    @Deprecated
    public static Date startOfDay(String time) {
        Date now = parseDataTime(time);
        Calendar c = Calendar.getInstance(); // 得到当前日期和时间
        c.setTime(now);
        c.set(Calendar.HOUR_OF_DAY, 0); // 把当前时间小时变成０
        c.set(Calendar.MINUTE, 0); // 把当前时间分钟变成０
        c.set(Calendar.SECOND, 0); // 把当前时间秒数变成０
        c.set(Calendar.MILLISECOND, 0); // 把当前时间毫秒变成０
        return c.getTime(); // 创建当天的0时0分0秒一个date对象
    }

    /**
     * 获取一天的0点 FIXME  非线程安全
     */
    @Deprecated
    public static Date startOfDay(long time) {
        return startOfDay(formatDateTime(time));
    }

    /**
     * 获取一天的0点 FIXME  非线程安全
     */
    @Deprecated
    public static Date startOfDay(Date time) {
        return startOfDay(formatDateTime(time));
    }

    /**
     * 取得指定时间的long型数据
     */
    public static long getTime(int day, String hour, String minute, String second) {
        Calendar c = Calendar.getInstance(); // 得到当前日期和时间
        c.set(Calendar.DAY_OF_YEAR, c.get(Calendar.DAY_OF_YEAR) + day);
        c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour)); // 设置小时
        c.set(Calendar.MINUTE, Integer.parseInt(minute)); // 设置分钟
        c.set(Calendar.SECOND, Integer.parseInt(second)); // 设置秒数
        c.set(Calendar.MILLISECOND, 0); // 把当前时间毫秒变成０
        return c.getTime().getTime();
    }

    /**
     * 取得指定时间的long型数据
     */
    public static long getTime(String hour, String minute, String second) {
        Calendar c = Calendar.getInstance(); // 得到当前日期和时间
        c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour)); // 设置小时
        c.set(Calendar.MINUTE, Integer.parseInt(minute)); // 设置分钟
        c.set(Calendar.SECOND, Integer.parseInt(second)); // 设置秒数
        c.set(Calendar.MILLISECOND, 0); // 把当前时间毫秒变成０
        return c.getTime().getTime();
    }

    /**
     * 取得当前是一周中的第几天<br/>
     * 1：周日
     * 2：周一
     * 3：周二
     * ...
     * 7：周六
     */
    public static int getWeek() {
        Calendar ca = Calendar.getInstance();
        return ca.get(Calendar.DAY_OF_WEEK);
    }

    public static int getWeek(long time) {
        Calendar ca = Calendar.getInstance();
        ca.setTimeInMillis(time);
        ;
        return ca.get(Calendar.DAY_OF_WEEK);
    }

    /**
     * 取得年份与周数的组合数值
     */
    public static long getCountWeek() {
        Calendar ca = Calendar.getInstance();
        int year = ca.get(Calendar.YEAR);
        int week = ca.get(Calendar.WEEK_OF_YEAR);
        return year * 100 + week;
    }

    /**
     * 比较当前时间是否在指定两个时间之间
     */
    public static boolean between(String start, String end) {
        Date d1 = parseDataTime(start);
        Date d2 = parseDataTime(end);
        return between(d1, d2);
    }

    /**
     * 比较当前时间是否在指定两个时间之间
     */
    public static boolean between(Date start, Date end) {
        Date now = new Date();
        if (start.before(now) && now.before(end)) {
            return true;
        }
        return false;
    }

    /**
     * 比较是否在指定时间之间(开区间)
     */
    public static boolean between1(Date start, Date end, Date middle) {
        if (start.before(middle) && middle.before(end)) {
            return true;
        }
        return false;
    }

    /**
     * 比较是否在指定时间之间(闭区间)
     */
    public static boolean between2(Date start, Date end, Date middle) {
        long s = start.getTime();
        long e = end.getTime();
        long m = middle.getTime();
        //
        if (s <= m && m <= e) {
            return true;
        }
        return false;
    }

    /**
     * 获得剩余时间字符串
     */
    public static String formatLeftTime(long time, String split) {
        if (time <= 0) {
            return "00";
        }

        long day = time / DAY;
        long hour = (time % DAY) / 3600;
        long min = (time % HOUR) / 60;
        long sec = (time % MINUTE) / 1;

        StringBuilder sb = new StringBuilder();
        if (day > 0) {
            if (day > 9)
                sb.append(day).append(split);
            else
                sb.append("0").append(day).append(split);
        }
        if (hour > 0) {
            if (hour > 9)
                sb.append(hour).append(split);
            else
                sb.append("0").append(hour).append(split);
        } else {
            sb.append("00").append(split);
        }
        if (min > 0) {
            if (min > 9)
                sb.append(min).append(split);
            else
                sb.append("0").append(min).append(split);
        } else {
            sb.append("00").append(split);
        }
        if (sec > 0) {
            if (sec > 9)
                sb.append(sec);
            else
                sb.append("0").append(sec);
        } else {
            sb.append("00");
        }
        return sb.toString();
    }

    /**
     * 获得剩余时间字符串(天/时/分/秒)
     */
    public static String formatLeftTime(long time) {
        if (time <= 0) {
            return "0" + SEC_STR;
        }
        long day = time / DAY;
        long hour = (time % DAY) / HOUR;
        long min = (time % HOUR) / MINUTE;
        long sec = (time % MINUTE) / SECOND;
        StringBuilder sb = new StringBuilder();
        boolean flag = false;
        if (day > 0) {
            flag = true;
            sb.append(day).append(DAY_STR);
        }
        if (flag || hour > 0) {
            flag = true;
            sb.append(hour).append(HOUR_STR);
        }
        if (flag || min > 0) {
            flag = true;
            sb.append(min).append(MINUTE_STR);
        }
        if (!flag || sec > 0) {
            sb.append(sec).append(SEC_STR);
        }
        return sb.toString();
    }

    /**
     * 获得剩余时间字符串(时/分/秒)
     */
    public static String formatLeftTime2(long time) {
        if (time <= 0) {
            return "0" + SEC_STR;
        }
        long day = time / DAY;
        long hour = (time % DAY) / HOUR;
        long min = (time % HOUR) / MINUTE;
        long sec = (time % MINUTE) / SECOND;
        StringBuilder sb = new StringBuilder();
        boolean flag = false;
        if (day > 0) {
            flag = true;
            // sb.append(day).append(DAY_STR).append(" ");
        }
        if (flag || hour > 0) {
            flag = true;
            sb.append(hour + day * 24).append(HOUR_STR).append("/");
        }
        if (flag || min > 0) {
            flag = true;
            sb.append(min).append(MINUTE_STR).append("/");
        }
        if (!flag || sec > 0) {
            sb.append(sec).append(SEC_STR);
        }
        return sb.toString();
    }

    /**
     * 计算两个time之间的毫秒数
     */
    public static long getBetweenTwoDaysTime(Date dt1, Date dt2) {
        long between = 0;
        if ((dt1.getTime() - dt2.getTime()) > 0) {
            between = dt1.getTime() - dt2.getTime();
        } else {
            between = dt2.getTime() - dt1.getTime();
        }
        return between;
    }

    /**
     * 根据当前时间获取几天前或几天后的时间
     */
    public static Date getNowTimeBeforeOrAfter(int days) {
        Long tempTime = System.currentTimeMillis() + DAY * days;
        Date timestamp = new Date(tempTime);
        return timestamp;
    }

    /**
     * 根据指定时间获取几天前或几天后的时间
     */
    public static Date getTimeBeforeOrAfter(long time, int days) {
        Long tempTime = time + DAY * days;
        Date timestamp = new Date(tempTime);
        return timestamp;
    }

    public static Date getTimeBeforeOrAfter(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int day = cal.get(Calendar.DATE);
        cal.set(Calendar.DATE, day + days);
        return cal.getTime();
    }

    /**
     * 计算两个time之间的时间差，计算包含年月日时分秒
     */
    public static String getBetweenTwoDaysTimeStr(Date dt1, Date dt2) {
        long between = 0;
        if ((dt1.getTime() - dt2.getTime()) > 0) {
            between = dt1.getTime() - dt2.getTime();
        } else {
            between = dt2.getTime() - dt1.getTime();
        }

        long day = between / DAY;
        long hour = (between - day * DAY) / HOUR;
        long min = (between - day * DAY - hour * HOUR) / MINUTE;
        long sec = (between - day * DAY - hour * HOUR - min * MINUTE) / SECOND;

        StringBuilder sb = new StringBuilder();
        sb.append(day).append(DAY_STR).append(hour).append(HOUR_STR)
                .append(min).append(MINUTE_STR).append(sec).append(SEC_STR);

        return sb.toString();
    }

    /**
     * 倒计时形式
     */
    public static String getCountdownTime(long time) {
        long day = time / DAY;
        long hour = (time - day * DAY) / HOUR;
        long min = (time - day * DAY - hour * HOUR) / MINUTE;
        long sec = (time - day * DAY - hour * HOUR - min * MINUTE) / SECOND;
        // 显示字符串
        String dayStr = String.valueOf(day);
        if (day < 10) {
            dayStr = "0" + day;
        }
        String hourStr = String.valueOf(hour);
        if (hour < 10) {
            hourStr = "0" + hour;
        }
        String minStr = String.valueOf(min);
        if (min < 10) {
            minStr = "0" + min;
        }
        String secStr = String.valueOf(sec);
        if (sec < 10) {
            secStr = "0" + sec;
        }
        StringBuilder sb = new StringBuilder();
        if (day <= 0) {
            sb.append(hourStr).append(":").append(minStr).append(":").append(secStr);
        } else {
            sb.append(dayStr).append(":").append(hourStr).append(":").append(minStr).append(":").append(secStr);
        }
        return sb.toString();
    }

    /**
     * 随机时间(固定时间段)
     */
    public static long randomTime(long begin, long end) {
        long time = begin + (long) (Math.random() * (end - begin));
        if (time == begin || time == end) {
            return randomTime(begin, end);
        }
        return time;
    }

    /**
     * 随机日期(固定日期段)
     */
    public static Date randomDate(Date beginDate, Date endDate) {
        if (beginDate.getTime() >= endDate.getTime()) {
            return null;
        }
        long date = randomTime(beginDate.getTime(), endDate.getTime());
        return new Date(date);
    }

    public static int getDayCount(int year, int month) {
        int daysInMonth[] = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        if (1 == month)
            return ((0 == year % 4) && (0 != (year % 100))) || (0 == year % 400) ? 29 : 28;
        else
            return daysInMonth[month];
    }

    /**
     * 计算剩余时间(毫秒数)
     */
    public static long getLeftTime(long lastTime, long nowTime) {
        if ((lastTime - nowTime) > 0) {
            return lastTime - nowTime;
        } else {
            return nowTime - lastTime;
        }
    }

    /**
     * 计算当天剩余时间(毫秒数)
     */
    public static long getLeftTime(long nowTime) {
        return getLeftTime(endOfDay().getTime(), nowTime);
    }

    /**
     * 计算剩余时间(日时分秒)
     */
    public static String getLeftTimeStr(long lastTime, long nowTime) {
        long between = getLeftTime(lastTime, nowTime); // 时间差,毫秒数
        // 时间差,天时分秒
        long day = between / DAY;
        long hour = (between - day * DAY) / HOUR;
        long min = (between - day * DAY - hour * HOUR) / MINUTE;
        long sec = (between - day * DAY - hour * HOUR - min * MINUTE) / SECOND;
        // 提示
        StringBuilder sb = new StringBuilder();
        if (day > 0) { // 天数
            sb.append(day).append(":");
        } else if (hour > 0) { // 小时数
            sb.append(hour).append(":");
        } else if (min > 0) { // 分钟数
            sb.append(min).append(":");
        } else if (sec > 0) { // 秒数
            sb.append(sec).append(":");
        }
        return sb.toString();
    }

    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=//

    //北京时区
    public static final long GMT8 = 8 * HOUR;

    public static final long GMT4 = 4 * HOUR;

    /**
     * 获取当天的0点时间	time:00:00:00
     *
     * @param time
     * @return
     */
    public static long getDayStartTime(long time) {
        return (time + GMT8) / DAY * DAY - GMT8;
    }

    /**
     * 获取当天的4点时间	time:04:00:00
     *
     * @param time
     * @return
     */
    public static long getDayMorning4(long time) {
        return (time + GMT4) / DAY * DAY - GMT4;
    }

    /**
     * 以0点为准判断同一天
     *
     * @param time1
     * @param time2
     * @return
     */
    public static boolean dayEqual(long time1, long time2) {
        return getDayStartTime(time1) == getDayStartTime(time2);
    }

    /**
     * 获取某一时刻的整点时间
     *
     * @return
     */
    public static long getLastClockTime(long time) {
        return time / HOUR * HOUR;
    }

    public static long getLastSpaceTime(long time, long space) {
        return time / space * space;
    }

    /**
     * 获取两天时间的天数差
     *
     * @param before
     * @param after
     * @return
     */
    public static int getDistanceDay(Date before, Date after) {
        return getDistanceDay(before.getTime(), after.getTime());
    }

    /**
     * 获取两天时间的天数差
     *
     * @param before
     * @param after
     * @return
     */
    public static int getDistanceDay(long before, long after) {
        return (int) (Math.abs(getDayStartTime(after) - getDayStartTime(before)) / DAY);
    }

    /**
     * 获取当前的一个周几某时刻的time值	以周日0点算起点
     *
     * @param dayOfWeek 0周日     1周一    ...  6周六
     * @param hour
     * @param minute
     * @param second
     * @return
     */
    public static long getCurrDayOfWeekTime(int dayOfWeek, int hour, int minute, int second) {
        long curr = System.currentTimeMillis();
        //1970-1-1是周四，算出当日所在周0的时间
        long initWeek0Time = 0 - 4 * DateUtil.DAY;
        //距周0过去多少天
        int day = (int) ((curr - initWeek0Time) / DateUtil.DAY);
        int week = day / 7;
        //当前周0时间
        long currWeek0Time = initWeek0Time + week * 7 * DateUtil.DAY - GMT8;
        //当前周dayOfWeek的时间
        long currDayOfWeekTime = currWeek0Time + dayOfWeek * DateUtil.DAY +
                hour * DateUtil.HOUR + minute * DateUtil.MINUTE + second * DateUtil.SECOND;
        //返回当前周的时间
        return currDayOfWeekTime;
    }

    /**
     * 获取当前的一个周几某时刻的time值	以周日0点算起点
     *
     * @param dayOfWeek  0周日     1周一    ...  6周六
     * @param timeFormat "hh:mm:ss"
     * @return
     */
    public static long getCurrDayOfWeekTime(int dayOfWeek, String timeFormat) {
        int hour = 0, minute = 0, second = 0;
        if (timeFormat != null) {
            String[] times = timeFormat.split(":");
            if (times != null) {
                if (times.length == 1) {
                    hour = Integer.valueOf(times[0]);
                } else if (times.length == 2) {
                    hour = Integer.valueOf(times[0]);
                    minute = Integer.valueOf(times[1]);
                } else if (times.length >= 3) {
                    hour = Integer.valueOf(times[0]);
                    minute = Integer.valueOf(times[1]);
                    second = Integer.valueOf(times[2]);
                }
            }
        }
        return getCurrDayOfWeekTime(dayOfWeek, hour, minute, second);
    }

    /**
     * 获取未到来的最近的一个周几某时刻的time值
     *
     * @param dayOfWeek 0周日     1周一    ...  6周六
     * @param hour
     * @param minute
     * @param second
     * @return
     */
    public static long getNextDayOfWeekTime(int dayOfWeek, int hour, int minute, int second) {
        long curr = System.currentTimeMillis();
        //当前周dayOfWeek的时间
        long currDayOfWeekTime = getCurrDayOfWeekTime(dayOfWeek, hour, minute, second);
        //返回当前周或者下周的时间
        if (currDayOfWeekTime >= curr)
            return currDayOfWeekTime;
        else
            return currDayOfWeekTime + DateUtil.WEEK;
    }

    /**
     * 获取未到来的最近的一个周几某时刻的time值
     *
     * @param dayOfWeek  0周日     1周一    ...  6周六
     * @param timeFormat "hh:mm:ss"
     * @return
     */
    public static long getNextDayOfWeekTime(int dayOfWeek, String timeFormat) {
        long curr = System.currentTimeMillis();
        //当前周dayOfWeek的时间
        long currDayOfWeekTime = getCurrDayOfWeekTime(dayOfWeek, timeFormat);
        //返回当前周或者下周的时间
        if (currDayOfWeekTime >= curr)
            return currDayOfWeekTime;
        else
            return currDayOfWeekTime + DateUtil.WEEK;
    }

    /**
     * 获取指定时间戳对应所在周周一0点的时间戳
     *
     * @param time
     * @return
     */
    public static long getWeekStartTime(long time) {
        LocalDateTime date = LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZONE_OFFSET).with(DayOfWeek.MONDAY);
        return date.toLocalDate().atStartOfDay().toInstant(ZONE_OFFSET).toEpochMilli();
    }

    public static boolean isSameDay(long t1, long t2) {
        return getDistanceDay(t1, t2) == 0;
    }

    /**
     * 获取通用周几表示
     * 1:周一
     * 2:...
     *
     * @return
     */
    public static int getWeekDay() {
        return LocalDateTime.now().getDayOfWeek().getValue();
    }
}
