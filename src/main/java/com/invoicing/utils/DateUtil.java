package com.invoicing.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期工具
 * 
 * @author fang
 *
 */
public class DateUtil {

	/**
	 * 获得一周的第一天时间
	 * 
	 * @return
	 */
	public static Date getFIRST_DAY_OF_WEEK() {
		Calendar calendar = Calendar.getInstance();
		calendar.setFirstDayOfWeek(Calendar.MONDAY);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		return calendar.getTime();
	}

	/**
	 * 取得一月中的第一天
	 * 
	 * @return
	 */
	public static Date get_FIRST_DAY_OF_MONTH() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}

	/**
	 * 取得一年中的第一天
	 * 
	 * @return
	 */
	public static Date get_FIRST_DAY_OF_YEAR() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_YEAR, 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}

	/**
	 * 字符串转换成时间
	 * 
	 * @param format
	 * @param time
	 * @return
	 * @throws ParseException
	 */
	public static Date stringToDate(String format, String time)
			throws ParseException {
		if (time == null || "".equals(time.trim()))
			return null;
		if (format == null)
			format = "yyyy-MM-dd";
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.parse(time);
	}

	/**
	 * 时间转成指定格式的字符串
	 * 
	 * @param format
	 * @param time
	 * @return
	 */
	public static String dateToString(String format, Date time) {
		if (time == null)
			return "";
		if (format == null)
			format = "yyyy-MM-dd HH:mm:ss";
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(time);
	}
}
