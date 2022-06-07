package com.cet.pq.pqgovernanceservice.util;

import org.springframework.util.StringUtils;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ParseDataUtil {

	/**
	 * 对象转Integer
	 * 
	 * @param obj
	 * @return
	 */
	public static Integer parseInteger(Object obj) {
		if (null == obj || obj == "") {
			return 0;
		} else {
			return Integer.parseInt(String.valueOf(obj).trim());
		}
	}

	/**
	 * 对象转Double
	 * 
	 * @param obj
	 * @return
	 */
	public static Double parseDouble(Object obj) {
		if (null == obj) {
			return 0D;
		} else {
			return Double.parseDouble(String.valueOf(obj).trim());
		}
	}

	/**
	 * 对象转Long
	 * 
	 * @param obj
	 * @return
	 */
	public static Long parseLong(Object obj) {
		if (null == obj) {
			return 0L;
		} else {
			return Long.parseLong(String.valueOf(obj).trim());
		}
	}

	/**
	 * 对象转String
	 * 
	 * @param obj
	 * @return
	 */
	public static String parseString(Object obj) {
		if (null == obj) {
			return "";
		} else {
			return String.valueOf(obj).trim();
		}
	}

	/**
	 * 对象转String
	 *
	 * @param obj
	 * @return
	 */
	public static Long parseDate(Object obj) {
		if (null == obj) {
			return 0L;
		} else {
			return Date.valueOf(String.valueOf(obj)).getTime();
		}
	}

	/**
	 * 对象转Boolean
	 * 
	 * @param obj
	 * @return
	 */
	public static Boolean parseBoolean(Object obj) {
		if (null == obj) {
			return Boolean.FALSE;
		} else {
			return Boolean.parseBoolean(String.valueOf(obj).trim());
		}
	}
	/**
	 * 对象转List
	 * 
	 * @param obj
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<T> parseList(Object obj) {
		if (null == obj || StringUtils.isEmpty(ParseDataUtil.parseString(obj)) || ParseDataUtil.parseString(obj).equals("[]")) {
			return new ArrayList<>();
		} else {
			return (List<T>) obj;
		}
	}

	/**
	 * 对象转Map
	 *
	 * @param obj
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static Map parseMap(Object obj) {
		if (null == obj || StringUtils.isEmpty(ParseDataUtil.parseString(obj))) {
			return new ConcurrentHashMap(1);
		} else {
			return (Map) obj;
		}
	}
}
