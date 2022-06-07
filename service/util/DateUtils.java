package com.cet.pq.pqgovernanceservice.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;


/**
 * @author dengxiaojie
 */
public class DateUtils {

	/**
	 * 年-月-日 时:分:秒 显示格式
	 */
	public static final String DATE_TO_STRING_DETAIAL_PATTERN = "yyyy-MM-dd HH:mm:ss";

	/**
	 * 年-月-日 显示格式
	 */
	public static final String DATE_TO_STRING_SHORT_PATTERN = "yyyy-MM-dd";

	/**
	 * 年-月 显示格式
	 */
	public static final String YYYY_MM = "yyyy-MM";
	private static final long ONEDAY = 86400000L;
	public static final String DF_YYYY_MM_DD_HH_MM_SS_SSS = "yyyy-MM-dd HH:mm:ss.SSS";
	public static final String DF_YYYY_MM_DD_HH_MM_SS = "yyyyMMddHHmmss";
	public static final String DF_YYYY_MM_DD_HH_MM_SS_SSSS = "yyyyMMddHHmmssSSS";

	private static Logger logger = LoggerFactory.getLogger(DateUtils.class);

	private static final String LONG_TIME = "yyyy-MM-dd HH:mm:ss.SSS";

	private int weeks = 0;

	public static String getExportDateString(long startTime, long endTime) {
		Date startT = new Date(startTime);
		Date endT = new Date(endTime);
		String dateFormat = "yyyyMMddHHmmss";
		return String.format("%s-%s", DateUtils.dateToStr(startT, dateFormat), DateUtils.dateToStr(endT, dateFormat));
	}

	public static String getExportSingleDateString(long startTime) {
		Date startT = new Date(startTime);
		String dateFormat = "yyyyMMddHHmmss";
		return String.format("%s", DateUtils.dateToStr(startT, dateFormat));
	}

	/**
	 * 日期转换成字符串
	 *
	 * @param date
	 * @return str
	 */
	public static String dateToStr(Date date) {

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		String str = format.format(date);
		return str;
	}

	/**
	 * 日期转换成字符串
	 *
	 * @param date
	 * @return str
	 */
	public static String dateToStr(Date date, String formatStr) {

		SimpleDateFormat format = new SimpleDateFormat(formatStr);
		String str = format.format(date);
		return str;
	}

	/**
	 * 通过年获取时间戳
	 *
	 * @param year
	 * @return
	 */
	public static long yearToTimeStamp(int year) {

		SimpleDateFormat format = new SimpleDateFormat("yyyy");
		Date date = null;
		long timestamp = 0;
		try {
			date = format.parse(String.valueOf(year));
			timestamp = date.getTime();
		} catch (ParseException e) {
			logger.debug("context", e);
		}
		return timestamp;
	}

	/**
	 * 得到二个日期间的间隔天数
	 */
	public static String getTwoDay(String sj1, String sj2) {
		SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd");
		long day = 0;
		try {
			Date date = myFormatter.parse(sj1);
			Date mydate = myFormatter.parse(sj2);
			day = (date.getTime() - mydate.getTime()) / (24 * 60 * 60 * 1000);
		} catch (Exception e) {
			return "";
		}
		return ParseDataUtil.parseString(day);
	}

	/**
	 * 根据一个日期，返回是星期几的字符串
	 *
	 * @param sdate
	 * @return
	 */
	public static String getWeek(String sdate) {
		// 再转换为时间
		Date date = DateUtils.strToDate(sdate);
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return new SimpleDateFormat("EEEE").format(c.getTime());
	}

	/**
	 * 获取传入年的开始时间的时间戳
	 *
	 * @return
	 */
	public static long setTime(Integer year, Integer month, Integer dayOfWeek, Integer day, Integer hour, Integer minute, Integer second, Integer milliseconds) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month-1);
		calendar.set(Calendar.WEEK_OF_MONTH, dayOfWeek);
		calendar.set(Calendar.DAY_OF_MONTH, day);
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, milliseconds);
		return calendar.getTimeInMillis();
	}

	/**
	 * 将短时间格式字符串转换为时间 yyyy-MM-dd
	 *
	 * @param strDate
	 * @return
	 */
	public static Date strToDate(String strDate) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		ParsePosition pos = new ParsePosition(0);
		Date strtodate = formatter.parse(strDate, pos);
		return strtodate;
	}

	public static Date strToDate(String timeStr, String formatStr) {

		SimpleDateFormat format = new SimpleDateFormat(formatStr);
		Date date = null;
		try {
			date = format.parse(timeStr);
		} catch (ParseException e) {
			logger.debug("context", e);
		}
		return date;
	}

	/**
	 * 将短时间格式字符串转换为时间 yyyy-MM-dd
	 *
	 * @param strDate
	 * @return
	 */
	public static Date strToLongDate(String strDate) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		ParsePosition pos = new ParsePosition(0);
		Date strtodate = formatter.parse(strDate, pos);
		return strtodate;
	}

	/**
	 * 两个时间之间的天数
	 *
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static long getDays(String date1, String date2) {
		if (date1 == null || "".equals(date1)) {
			return 0;
		}
		if (date2 == null || "".equals(date2)) {
			return 0;
		}
		// 转换为标准时间
		SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd");
		Date date = null;
		Date mydate = null;
		long day = 0;
		try {
			date = myFormatter.parse(date1);
			mydate = myFormatter.parse(date2);
			day = (date.getTime() - mydate.getTime()) / (24 * 60 * 60 * 1000);
		} catch (Exception e) {
			logger.debug("context", e);
		}
		return day;
	}

	
	public static long getDays(Long date1, Long date2) {
		long days = (date2-date1) / (24 * 60 * 60 * 1000);
		return days;
	}
	
	/**
	 * 获取两个时间差（分钟）
	 *
	 * @param begin 起始时间
	 * @param end   结束时间
	 * @return
	 */
	public static double getDiffMinutes(Date begin, Date end) {
		long between = (end.getTime() - begin.getTime());
		return ((double) between) / 1000 / 60;
	}

	public String getDefaultDay() {
		String str = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		Calendar lastDate = Calendar.getInstance();
		lastDate.set(Calendar.DATE, 1);
		lastDate.add(Calendar.MONTH, 1);
		lastDate.add(Calendar.DATE, -1);

		str = sdf.format(lastDate.getTime());
		return str;
	}

	public String getPreviousMonthFirst() {
		String str = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		Calendar lastDate = Calendar.getInstance();
		lastDate.set(Calendar.DATE, 1);
		lastDate.add(Calendar.MONTH, -1);
		str = sdf.format(lastDate.getTime());
		return str;
	}

	/**
	 * 获取当月第一天
	 * 
	 * @return
	 */
	public static Date getFirstDayOfMonth() {
		String str = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		Calendar lastDate = Calendar.getInstance();
		lastDate.set(Calendar.DATE, 1);
		str = sdf.format(lastDate.getTime());
		return strToDate(str, "yyyy-MM-dd");
	}

	public static Date getFirstDayOfMonth(Date oriTime) {
		String str = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		Calendar lastDate = Calendar.getInstance();
		lastDate.setTime(oriTime);
		lastDate.set(Calendar.DATE, 1);
		str = sdf.format(lastDate.getTime());
		return strToDate(str, "yyyy-MM-dd");
	}



	/**
	 * 获取当天的开始时间 0时0分0秒0毫秒
	 * 
	 * @return
	 */
	public static Long getFirstTimeOfDay() {
		String str = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.sss");
		Calendar lastDate = Calendar.getInstance();
		lastDate.set(Calendar.HOUR_OF_DAY, 0);
		lastDate.set(Calendar.MINUTE, 0);
		lastDate.set(Calendar.SECOND, 0);
		lastDate.set(Calendar.MILLISECOND, 0);
		str = sdf.format(lastDate.getTime());
		return strToDate(str, "yyyy-MM-dd HH:mm:ss.sss").getTime();
	}

	/**
	 * 获取当天的开始时间 0时0分0秒0毫秒
	 *
	 * @return
	 */
	public static Long getFirstTimeOfDay1(Long time) {
		String str = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.sss");
		Calendar lastDate = Calendar.getInstance();
		lastDate.setTime(new Date(time));
		lastDate.set(Calendar.HOUR_OF_DAY, 0);
		lastDate.set(Calendar.MINUTE, 0);
		lastDate.set(Calendar.SECOND, 0);
		lastDate.set(Calendar.MILLISECOND, 0);
		str = sdf.format(lastDate.getTime());
		return strToDate(str, "yyyy-MM-dd HH:mm:ss.sss").getTime();
	}

	/**
	 * 获取当天的开始时间 0时0分0秒0毫秒
	 *
	 * @return
	 */
	public static Long getFirstTimeOfDay(Long time) {
		String str = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.sss");
		Calendar lastDate = Calendar.getInstance();
		lastDate.setTime(new Date(time));
		lastDate.set(Calendar.HOUR_OF_DAY, 24);
		lastDate.set(Calendar.MINUTE, 0);
		lastDate.set(Calendar.SECOND, 0);
		lastDate.set(Calendar.MILLISECOND, 0);
		str = sdf.format(lastDate.getTime());
		return strToDate(str, "yyyy-MM-dd HH:mm:ss.sss").getTime();
	}

	/**
	 * 获取昨天的开始时间 0时0分0秒0毫秒
	 *
	 * @return
	 */
	public static Long getPreFirstTimeOfDay() {
		String str = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.sss");
		Calendar lastDate = Calendar.getInstance();
		lastDate.set(Calendar.HOUR_OF_DAY, -24);
		lastDate.set(Calendar.MINUTE, 0);
		lastDate.set(Calendar.SECOND, 0);
		lastDate.set(Calendar.MILLISECOND, 0);
		str = sdf.format(lastDate.getTime());
		return strToDate(str, "yyyy-MM-dd HH:mm:ss.sss").getTime();
	}


	/**
	 * 获取当天的开始时间 0时0分0秒0毫秒
	 *
	 * @return
	 */
	public static Long getLastTimeOfDay() {
		String str = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.sss");
		Calendar lastDate = Calendar.getInstance();
		lastDate.set(Calendar.HOUR_OF_DAY, 23);
		lastDate.set(Calendar.MINUTE, 59);
		lastDate.set(Calendar.SECOND, 59);
		lastDate.set(Calendar.MILLISECOND, 999);
		str = sdf.format(lastDate.getTime());
		return strToDate(str, "yyyy-MM-dd HH:mm:ss.sss").getTime();
	}

	/**
	 * 获取当天结束时间 0时0分0秒0毫秒
	 *
	 * @return
	 */
	public static Long getLastTimeOfDay(Long time) {
		String str = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.sss");
		Calendar lastDate = Calendar.getInstance();
		lastDate.setTime(new Date(time));
		lastDate.set(Calendar.HOUR_OF_DAY, 23);
		lastDate.set(Calendar.MINUTE, 59);
		lastDate.set(Calendar.SECOND, 59);
		lastDate.set(Calendar.MILLISECOND, 999);
		str = sdf.format(lastDate.getTime());
		return strToDate(str, "yyyy-MM-dd HH:mm:ss.sss").getTime();
	}

	/**
	 * 获取前两个小时
	 *
	 * @return
	 */
	public static Long getPreTwoHour() {
		String str = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.sss");
		Calendar lastDate = Calendar.getInstance();
		lastDate.set(Calendar.HOUR_OF_DAY, lastDate.get(Calendar.HOUR_OF_DAY) - 2);
		lastDate.set(Calendar.MINUTE, 0);
		lastDate.set(Calendar.SECOND, 0);
		lastDate.set(Calendar.MILLISECOND, 0);
		str = sdf.format(lastDate.getTime());
		return strToDate(str, "yyyy-MM-dd HH:mm:ss.sss").getTime();
	}

	/**
	 * 获取当前小时
	 *
	 * @return
	 */
	public static Long getCurrentHour() {
		String str = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.sss");
		Calendar lastDate = Calendar.getInstance();
		lastDate.set(Calendar.HOUR_OF_DAY, lastDate.get(Calendar.HOUR_OF_DAY));
		lastDate.set(Calendar.MINUTE, 0);
		lastDate.set(Calendar.SECOND, 0);
		lastDate.set(Calendar.MILLISECOND, 0);
		str = sdf.format(lastDate.getTime());
		return strToDate(str, "yyyy-MM-dd HH:mm:ss.sss").getTime();
	}

	/**
	 * 获取当天的开始时间 0时0分0秒0毫秒
	 * 
	 * @param oriTime
	 * @return
	 */
	public static Date getFirstTimeOfDay(Date oriTime) {
		String str = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.sss");
		Calendar lastDate = Calendar.getInstance();
		lastDate.setTime(oriTime);
		lastDate.set(Calendar.HOUR_OF_DAY, 0);
		lastDate.set(Calendar.MINUTE, 0);
		lastDate.set(Calendar.SECOND, 0);
		lastDate.set(Calendar.MILLISECOND, 0);
		str = sdf.format(lastDate.getTime());
		return strToDate(str, "yyyy-MM-dd HH:mm:ss.sss");
	}

	/**
	 * 获取当天的结束时间
	 * 
	 * @param oriTime
	 * @return
	 */
	public static Date getLastTimeOfDay(Date oriTime) {
		String str = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.sss");
		Calendar lastDate = Calendar.getInstance();
		lastDate.setTime(oriTime);
		lastDate.set(Calendar.HOUR_OF_DAY, 23);
		lastDate.set(Calendar.MINUTE, 59);
		lastDate.set(Calendar.SECOND, 59);
		str = sdf.format(lastDate.getTime());
		return strToDate(str, "yyyy-MM-dd HH:mm:ss.sss");
	}

	/**
	 * 获得本周星期日的日期
	 * 
	 * @return
	 */
	public String getCurrentWeekday() {
		weeks = 0;
		int mondayPlus = this.getMondayPlus();
		GregorianCalendar currentDate = new GregorianCalendar();
		currentDate.add(GregorianCalendar.DATE, mondayPlus + 6);
		Date monday = currentDate.getTime();

		DateFormat df = DateFormat.getDateInstance();
		String preMonday = df.format(monday);
		return preMonday;
	}

	/**
	 * 获取当天时间
	 * 
	 * @param dateformat
	 * @return
	 */
	public String getNowTime(String dateformat) {
		Date now = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat(dateformat);
		String hehe = dateFormat.format(now);
		return hehe;
	}

	/**
	 * 获得当前日期与本周日相差的天数
	 * 
	 * @return
	 */
	private int getMondayPlus() {
		Calendar cd = Calendar.getInstance();
		int dayOfWeek = cd.get(Calendar.DAY_OF_WEEK) - 1;
		if (dayOfWeek == 1) {
			return 0;
		} else {
			return 1 - dayOfWeek;
		}
	}

	/**
	 * 获得本周一的日期
	 * 
	 * @return
	 */
	public String getMondayOfWeek() {
		weeks = 0;
		int mondayPlus = this.getMondayPlus();
		GregorianCalendar currentDate = new GregorianCalendar();
		currentDate.add(GregorianCalendar.DATE, mondayPlus);
		Date monday = currentDate.getTime();

		DateFormat df = DateFormat.getDateInstance();
		String preMonday = df.format(monday);
		return preMonday;
	}

	/**
	 * 获得相应周的周六的日期
	 * 
	 * @return
	 */
	public String getSaturday() {
		int mondayPlus = this.getMondayPlus();
		GregorianCalendar currentDate = new GregorianCalendar();
		currentDate.add(GregorianCalendar.DATE, mondayPlus + 7 * weeks + 6);
		Date monday = currentDate.getTime();
		DateFormat df = DateFormat.getDateInstance();
		String preMonday = df.format(monday);
		return preMonday;
	}

	/**
	 * 获得上周星期日的日期
	 * 
	 * @return
	 */
	public String getPreviousWeekSunday() {
		weeks = 0;
		weeks--;
		int mondayPlus = this.getMondayPlus();
		GregorianCalendar currentDate = new GregorianCalendar();
		currentDate.add(GregorianCalendar.DATE, mondayPlus + weeks);
		Date monday = currentDate.getTime();
		DateFormat df = DateFormat.getDateInstance();
		String preMonday = df.format(monday);
		return preMonday;
	}

	/**
	 * 获得上周星期一的日期
	 * 
	 * @return
	 */
	public String getPreviousWeekday() {
		weeks--;
		int mondayPlus = this.getMondayPlus();
		GregorianCalendar currentDate = new GregorianCalendar();
		currentDate.add(GregorianCalendar.DATE, mondayPlus + 7 * weeks);
		Date monday = currentDate.getTime();
		DateFormat df = DateFormat.getDateInstance();
		String preMonday = df.format(monday);
		return preMonday;
	}

	/**
	 * 获得下周星期一的日期
	 * 
	 * @return
	 */
	public String getNextMonday() {
		weeks++;
		int mondayPlus = this.getMondayPlus();
		GregorianCalendar currentDate = new GregorianCalendar();
		currentDate.add(GregorianCalendar.DATE, mondayPlus + 7);
		Date monday = currentDate.getTime();
		DateFormat df = DateFormat.getDateInstance();
		String preMonday = df.format(monday);
		return preMonday;
	}

	/**
	 * 获得下周星期日的日期
	 * 
	 * @return
	 */
	public String getNextSunday() {

		int mondayPlus = this.getMondayPlus();
		GregorianCalendar currentDate = new GregorianCalendar();
		currentDate.add(GregorianCalendar.DATE, mondayPlus + 7 + 6);
		Date monday = currentDate.getTime();
		DateFormat df = DateFormat.getDateInstance();
		String preMonday = df.format(monday);
		return preMonday;
	}

	/**
	 * 获得上月最后一天的日期
	 * 
	 * @return
	 */
	public String getPreviousMonthEnd() {
		String str = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		Calendar lastDate = Calendar.getInstance();
		lastDate.add(Calendar.MONTH, -1);
		lastDate.set(Calendar.DATE, 1);
		lastDate.roll(Calendar.DATE, -1);
		str = sdf.format(lastDate.getTime());
		return str;
	}

	/**
	 * 获得下个月第一天的日期
	 * 
	 * @param oriTime
	 * @return
	 */
	public static Date getNextMonthFirst(Date oriTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar lastDate = Calendar.getInstance();
		lastDate.setTime(oriTime);
		lastDate.add(Calendar.MONTH, 1);
		lastDate.set(Calendar.DATE, 1);
		String str = sdf.format(lastDate.getTime());
		return strToDate(str);
	}

	/**
	 * 获得下个月最后一天的日期
	 * 
	 * @param date
	 * @return
	 */
	public static Date getNextMonthEnd(Date date) {
		Calendar lastDate = Calendar.getInstance();
		lastDate.setTime(date);
		lastDate.add(Calendar.MONTH, 1);
		lastDate.set(Calendar.DATE, 1);
		lastDate.roll(Calendar.DATE, -1);
		return lastDate.getTime();
	}

	/**
	 * 获得当前时间小时
	 *
	 * @param date
	 * @return
	 */
	public static String getCurrentHour(Date date) {
		Calendar lastDate = Calendar.getInstance();
		lastDate.setTime(date);
		lastDate.set(Calendar.MINUTE, 0);
		lastDate.set(Calendar.SECOND, 0);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		return sdf.format(lastDate.getTime());
	}

	/**
	 * 获得当前时间小时
	 *
	 * @param date
	 * @return
	 */
	public static Long getCurrentPreHour(Date date) {
		Calendar lastDate = Calendar.getInstance();
		lastDate.setTime(date);
		lastDate.set(Calendar.HOUR, lastDate.get(Calendar.HOUR) - 1);
		lastDate.set(Calendar.MINUTE, 0);
		lastDate.set(Calendar.SECOND, 0);
		lastDate.set(Calendar.MILLISECOND, 0);
		return lastDate.getTime().getTime();
	}

	/**
	 * 获得当前时间小时
	 *
	 * @param date
	 * @return
	 */
	public static Long getCurrentPreLastHour(Date date) {
		Calendar lastDate = Calendar.getInstance();
		lastDate.setTime(date);
		lastDate.set(Calendar.HOUR, lastDate.get(Calendar.HOUR) - 1);
		lastDate.set(Calendar.MINUTE, 59);
		lastDate.set(Calendar.SECOND, 59);
		lastDate.set(Calendar.MILLISECOND, 0);
		return lastDate.getTime().getTime();
	}

	/**
	 * 获得小时最后
	 *
	 * @param date
	 * @return
	 */
	public static String getNextHour(Date date) {
		Calendar lastDate = Calendar.getInstance();
		lastDate.setTime(date);
		lastDate.set(Calendar.MINUTE, 59);
		lastDate.set(Calendar.SECOND, 59);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		return sdf.format(lastDate.getTime());
	}

	public static String getNextMonthEnd() {
		String str = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		Calendar lastDate = Calendar.getInstance();
		lastDate.add(Calendar.MONTH, 1);
		lastDate.set(Calendar.DATE, 1);
		lastDate.roll(Calendar.DATE, -1);
		str = sdf.format(lastDate.getTime());
		return str;
	}

	/**
	 * 获得上个月最后一天的日期
	 * 
	 * @param date
	 * @return
	 */
	public static Date getLastMonthEnd(Date date) {
		Calendar lastDate = Calendar.getInstance();
		lastDate.setTime(date);
		lastDate.add(Calendar.MONTH, -1);
		lastDate.set(Calendar.DATE, 1);
		lastDate.roll(Calendar.DATE, -1);
		return lastDate.getTime();
	}

	/**
	 * 获得明年最后一天的日期
	 * 
	 * @return
	 */
	public String getNextYearEnd() {
		String str = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		Calendar lastDate = Calendar.getInstance();
		lastDate.add(Calendar.YEAR, 1);
		lastDate.set(Calendar.DAY_OF_YEAR, 1);
		lastDate.roll(Calendar.DAY_OF_YEAR, -1);
		str = sdf.format(lastDate.getTime());
		return str;
	}

	/**
	 * 获得明年第一天的日期
	 * 
	 * @return
	 */
	public static String getNextYearFirst() {
		String str = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		Calendar lastDate = Calendar.getInstance();
		lastDate.add(Calendar.YEAR, 1);
		lastDate.set(Calendar.DAY_OF_YEAR, 1);
		str = sdf.format(lastDate.getTime());
		return str;

	}

	/**
	 * @param i 年偏移量
	 * @return
	 */
	public static String getYearTimeByInput(int i) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
		Calendar lastDate = Calendar.getInstance();
		lastDate.add(Calendar.YEAR, i);
		String years = sdf.format(lastDate.getTime());
		return years + "-01-01";
	}

	/**
	 * 获得年有多少天
	 * 
	 * @param year
	 * @return
	 */
	public static int getMaxYear(int year) {
		Calendar cd = Calendar.getInstance();
		cd.set(Calendar.YEAR, year);
		cd.set(Calendar.DAY_OF_YEAR, 1);
		cd.roll(Calendar.DAY_OF_YEAR, -1);
		int maxYear = cd.get(Calendar.DAY_OF_YEAR);
		return maxYear;
	}

	private static int getYearPlus() {
		Calendar cd = Calendar.getInstance();
		int yearOfNumber = cd.get(Calendar.DAY_OF_YEAR);
		cd.set(Calendar.DAY_OF_YEAR, 1);
		cd.roll(Calendar.DAY_OF_YEAR, -1);
		int maxYear = cd.get(Calendar.DAY_OF_YEAR);
		if (yearOfNumber == 1) {
			return -maxYear;
		} else {
			return 1 - yearOfNumber;
		}
	}

	/**
	 * 获得本年第一天的日期
	 * 
	 * @return
	 */
	public static String getCurrentYearFirst() {
		int yearPlus = getYearPlus();
		GregorianCalendar currentDate = new GregorianCalendar();
		currentDate.add(GregorianCalendar.DATE, yearPlus);
		Date yearDay = currentDate.getTime();
		DateFormat df = DateFormat.getDateInstance();
		String preYearDay = df.format(yearDay);
		return preYearDay;
	}

	/**
	 * 获得本年最后一天的日期
	 * 
	 * @return
	 */
	public static String getCurrentYearEnd() {
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");
		String years = dateFormat.format(date);
		return years + "-12-31";
	}

	/**
	 * 获得上年第一天的日期
	 * 
	 * @return
	 */
	public String getPreviousYearFirst() {
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");
		String years = dateFormat.format(date);
		int yearsValue = Integer.parseInt(years);
		yearsValue--;
		return yearsValue + "-1-1";
	}

	/**
	 * 是否闰年
	 * 
	 * @param year 年
	 * @return
	 */
	public boolean isLeapYear(int year) {
		return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
	}

	public static int getYear(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(Calendar.YEAR);
	}

	/**
	 * 功能描述：返回月
	 *
	 * @param date Date 日期
	 * @return 返回月份
	 */
	public static int getMonth(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(Calendar.MONTH) + 1;
	}

	/**
	 * 功能描述：返回日
	 *
	 * @param date Date 日期
	 * @return 返回日份
	 */
	public static int getDay(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(Calendar.DATE);
	}

	/**
	 * 功能描述：返回小
	 *
	 * @param date 日期
	 * @return 返回小时
	 */
	public static int getHour(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(Calendar.HOUR_OF_DAY);
	}

	public static int getMinute(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(Calendar.MINUTE);
	}

	public static int getSecond(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(Calendar.SECOND);
	}

	public static int getMillisecond(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(Calendar.MILLISECOND);
	}

	/**
	 * 时间计算
	 *
	 * @param date   当前日期
	 * @param field  时间部分
	 * @param amount 加减数量
	 * @return
	 */
	public static Date dateCalc(Date date, int field, int amount) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(field, amount);
		return calendar.getTime();
	}

	/**
	 * Date类型转为指定格式的String类型
	 *
	 * @param source
	 * @param pattern
	 * @return
	 */
	public static String dateToString(Date source, String pattern) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		return simpleDateFormat.format(source);
	}

	/**
	 * unix时间戳转为指定格式的String类型
	 * <p>
	 * <p>
	 * System.currentTimeMillis()获得的是是从1970年1月1日开始所经过的毫秒数
	 * unix时间戳:是从1970年1月1日（UTC/GMT的午夜）开始所经过的秒数,不考虑闰秒
	 *
	 * @param source
	 * @param pattern
	 * @return
	 */
	public static String timeStampToString(long source, String pattern) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		Date date = new Date(source * 1000);
		return simpleDateFormat.format(date);
	}

	/**
	 * 格式化时间戳
	 *
	 * @param source
	 * @param pattern
	 * @return
	 */
	public static String formatDate(long source, String pattern) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		Date date = new Date(source);
		return simpleDateFormat.format(date);
	}

	/**
	 * 获取当月时间戳
	 *
	 * @param date
	 * @return
	 */
	public static long getThisMonthTimeStamp(Date date) throws ParseException {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM");
		String formatMmdate = simpleDateFormat.format(date);
		Date formatDate = simpleDateFormat.parse(formatMmdate);
		return formatDate.getTime();
	}

	/**
	 * @param date
	 * @param i    偏移月份
	 * @return
	 */
	public static long getNextMonthTimeStamp(Date date, int i) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM");
		String formatMmDate = simpleDateFormat.format(date);
		Date formatDate;
		try {
			formatDate = simpleDateFormat.parse(formatMmDate);
			Calendar cal = Calendar.getInstance();
			cal.setTime(formatDate);
			cal.add(Calendar.MONTH, i);
			return cal.getTimeInMillis();
		} catch (ParseException e) {
			logger.error(formatMmDate);
		}
		return 0;
	}

	/**
	 * @param date
	 * @param i    偏移月份
	 * @return
	 */
	public static Date getNextMonthDate(Date date, int i) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM");
		String formatMmDate = simpleDateFormat.format(date);
		Date formatDate;
		try {
			formatDate = simpleDateFormat.parse(formatMmDate);
			Calendar cal = Calendar.getInstance();
			cal.setTime(formatDate);
			cal.add(Calendar.MONTH, i);
			return cal.getTime();
		} catch (ParseException e) {
			logger.error(formatMmDate);
		}
		return new Date();
	}

	/**
	 * 获取偏移月后的月份
	 * 
	 * @param source
	 * @param i
	 * @return
	 */
	public static int getOffsetMonth(Long source, int i) throws ParseException {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String d = simpleDateFormat.format(source);
		Calendar cal = Calendar.getInstance();
		cal.setTime(simpleDateFormat.parse(d));
		cal.add(Calendar.MONTH, i);
		return cal.get(Calendar.MONTH) + 1;
	}


	/**
	 * @param source
	 * @return
	 */
	public static String getStringDateByTimeStamp(long source){
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String d = simpleDateFormat.format(source);
		return d;
	}

	/**
	 * 获取年月时间字符串
	 *
	 * @param source
	 * @return
	 */
	public static String getYmdsSateByTimeStamp(long source) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DF_YYYY_MM_DD_HH_MM_SS_SSS);
		String d = simpleDateFormat.format(source);
		return d;
	}

	/**
	 * 获取年月时间字符串
	 * 
	 * @param source
	 * @return
	 */
	public static String getYmDateByTimeStamp(long source) throws ParseException {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM");
		String d = simpleDateFormat.format(source);
		return d;
	}

	/**
	 * 获取年月时间字符串
	 *
	 * @param source
	 * @return
	 */
	public static String getYmDateByDate(Date source) throws ParseException {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM");
		String d = simpleDateFormat.format(source);
		return d;
	}

	/**
	 * 时间戳获取月
	 *
	 * @param source
	 * @return
	 */
	public static int getMonthByTimeStamp(long source) throws ParseException {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String d = simpleDateFormat.format(source);
		Calendar cal = Calendar.getInstance();
		cal.setTime(simpleDateFormat.parse(d));
		return cal.get(Calendar.MONTH) + 1;
	}

	/**
	 * 将日期转换为时间戳(unix时间戳,单位秒)
	 *
	 * @param date
	 * @return
	 */
	public static long dateToTimeStamp(Date date) {
		Timestamp timestamp = new Timestamp(date.getTime());
		return timestamp.getTime();

	}

	public static Date timestampToDate(long timestamp) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateStr = format.format(timestamp);
		Date date = null;
		try {
			date = format.parse(dateStr);
		} catch (Exception ex) {
			return date;
		}

		return date;
	}

	/**
	 * 字符串转换为对应日期(可能会报错异常)
	 *
	 * @param source
	 * @param pattern
	 * @return
	 */
	public static Date stringToDate(String source, String pattern) throws ParseException {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		Date date = simpleDateFormat.parse(source);
		return date;
	}

	/**
	 * 获得当前时间对应的指定格式
	 *
	 * @param pattern
	 * @return
	 */
	public static String currentFormatDate(String pattern) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		return simpleDateFormat.format(new Date());

	}

	/**
	 * 获得当前unix时间戳(单位秒)
	 *
	 * @return 当前unix时间戳
	 */
	public static long currentTimeStamp() {
		return System.currentTimeMillis() / 1000;
	}


	/**
	 * 获取当年的开始时间的时间戳
	 *
	 * @return
	 */
	public static long getFirstDayOfYear() {
		Integer year = getYear(new Date());
		return getFirstDayOfYear(year);
	}

	/**
	 * 获取传入年的开始时间的时间戳
	 *
	 * @return
	 */
	public static long getFirstDayOfYear(Integer year) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, 0);
		return calendar.getTimeInMillis();
	}

	/**
	 * 获取传入时间的开始时间的时间戳
	 *
	 * @return
	 */
	public static long getFirstDayOfYear(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return getFirstDayOfYear(calendar.get(Calendar.YEAR));
	}

	/**
	 * 获取两个时间戳之间的天数
	 *
	 * @return
	 */
	public static Long calculateDay(Long starttime,Long endTime) {
		Calendar calendar = Calendar.getInstance();
		Date date = new Date(System.currentTimeMillis());
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		Long nextDayTime = calendar.getTimeInMillis() + ONEDAY;
		if(endTime > nextDayTime){
			return (nextDayTime-starttime)/ONEDAY;
		}else {
			return (endTime-starttime)/ONEDAY;
		}
	}



	/**
	 * 获取传入年结束时间戳
	 *
	 * @return
	 */
	public static long getLastTimeOfYear(Integer year) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.DAY_OF_MONTH, 30);
		calendar.set(Calendar.MONTH, 11);
		return calendar.getTimeInMillis();
	}

	/**
	 * 判断两个时间戳指定的时间字段是否相等
	 *
	 * @param time1        时间1
	 * @param time2        时间2
	 * @param compareItems 需要对比的时间字段，为Calendar中定义的枚举； 如果该值为空，那么直接返回两个时间戳的对比
	 * @return 两个时间是否相等
	 */
	public static boolean isEquals(long time1, long time2, List<Integer> compareItems) {
		if (CollectionUtils.isEmpty(compareItems)) {
			return time1 == time2;
		}
		Calendar calendar1 = Calendar.getInstance();
		calendar1.setTimeInMillis(time1);

		Calendar calendar2 = Calendar.getInstance();
		calendar2.setTimeInMillis(time2);
		for (Integer compareItem : compareItems) {
			if (calendar1.get(compareItem) != calendar2.get(compareItem)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * 获取特定年月的时间戳，日默认为1
	 * 
	 * @param year
	 * @param month
	 * @return
	 */
	public static long getSpecificTimeByYearAndMonth(int year, int month) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month, 1, 0, 0, 0);
		return calendar.getTimeInMillis();
	}

	/**
	 * 获取特定小时分钟的时间戳，日默认为1
	 *
	 * @param hour
	 * @param minute
	 * @return
	 */
	public static long getSpecificTimeByHourAndMinute(int hour, int minute) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		return calendar.getTimeInMillis();
	}

	/**
	 * 字符串转时间戳
	 * 
	 * @param time
	 **/
	public static Long getTimestamp(String time) {
		Long timestamp = null;
		try {
			timestamp = new SimpleDateFormat("yyyy-MM-dd").parse(time).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return timestamp;
	}

	/**
	 * date转LocalDateTime
	 **/
	public static LocalDateTime convertLocalDateTime(Date date) {
		Instant instant = date.toInstant();
		ZoneId zoneId = ZoneId.systemDefault();
		LocalDateTime localDateTime = instant.atZone(zoneId).toLocalDateTime();
		return localDateTime;
	}
}
