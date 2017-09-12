package com.thread.util;


import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import com.alibaba.fastjson.JSON;

public class SecurityUtils {

	private static Map<String, String> escapeMap = new HashMap<String, String>();
	static {
		escapeMap.put("&", "&amp;");
		escapeMap.put("<", "&lt;");
		escapeMap.put(">", "&gt;");
		escapeMap.put("'", "&#39;");
		escapeMap.put("\"", "&quot;");
	}

	public static <T> String maskPIdNoForShow(String input, Class<T> clazz, String[] fieldList) {
		return maskPIdNo(input, clazz, fieldList, Type.forShow);
	}

	public static <T> String maskPIdNoForLog(String input, Class<T> clazz, String[] fieldList) {
		return maskPIdNo(input, clazz, fieldList, Type.forLog);
	}

	private static <T> String maskPIdNo(String input, Class<T> clazz, String[] fieldList, Type type) {
		if (StringUtils.isEmpty(input)) {
			return null;
		}
		T obj = JSON.parseObject(input, clazz);
		for (String fieldName : fieldList) {
			Object value = ReflectionUtil.getValue(obj, fieldName);
			if (value != null && value instanceof String) {
				String pIdNo = (String) value;
				ReflectionUtil.setValue(obj, fieldName, maskPIdNo(type, pIdNo));
			}
		}
		return JSON.toJSONString(obj);
	}

	public static String maskPIdNoForLog(String pIdNo) {
		return maskPIdNo(Type.forShow, pIdNo);
	}

	private static String maskPIdNo(Type type, String pIdNo) {
		if (pIdNo.length() == 18 || pIdNo.length() == 15) {
			StringBuilder sb = new StringBuilder();
			if (type == Type.forLog) {
				sb.append(pIdNo.subSequence(0, 6));
			} else {
				sb.append("******");
			}
			for (int i = 0; i < pIdNo.length() - 10; i++) {
				sb.append("*");
			}
			sb.append(pIdNo.subSequence(pIdNo.length() - 4, pIdNo.length()));
			return sb.toString();
		} else {
			return "***invalidIdNo***";
		}
	}

	public static String maskPNameForShow(String name) {
		if (StringUtils.isEmpty(name)) {
			return null;
		}
		return "*" + name.substring(1, name.length());
	}

	public static <T> String maskBankCardNoForLog(String input, Class<T> clazz, String[] fieldList) {
		if (StringUtils.isEmpty(input)) {
			return null;
		}
		T obj = JSON.parseObject(input, clazz);
		for (String fieldName : fieldList) {
			Object value = ReflectionUtil.getValue(obj, fieldName);
			if (value != null && value instanceof String) {
				String bankCardNo = (String) value;
				ReflectionUtil.setValue(obj, fieldName, maskBankNo(bankCardNo));
			}
		}
		return JSON.toJSONString(obj);
	}

	private static String maskBankNo(String bankCardNo) {
		if (StringUtils.isEmpty(bankCardNo)) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		if (bankCardNo.length() < 9) {
			for (int i = 0; i < bankCardNo.length(); i++) {
				sb.append("*");
			}
			return sb.toString();
		}
		sb.append(bankCardNo.subSequence(0, 4));
		for (int i = 4; i < bankCardNo.length() - 4; i++) {
			sb.append("*");
		}
		sb.append(bankCardNo.subSequence(bankCardNo.length() - 4, bankCardNo.length()));
		return sb.toString();
	}

	public static <T> String maskMobileNoForLog(String input, Class<T> clazz, String[] fieldList) {
		if (StringUtils.isEmpty(input)) {
			return null;
		}
		T obj = JSON.parseObject(input, clazz);
		for (String fieldName : fieldList) {
			Object value = ReflectionUtil.getValue(obj, fieldName);
			if (value != null && value instanceof String) {
				String bankCardNo = (String) value;
				ReflectionUtil.setValue(obj, fieldName, maskMobileNo(bankCardNo));
			}
		}
		return JSON.toJSONString(obj);
	}

	private static String maskMobileNo(String mobileNo) {
		if (StringUtils.isEmpty(mobileNo)) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		if (mobileNo.length() < 7) {
			for (int i = 0; i < mobileNo.length(); i++) {
				sb.append("*");
			}
			return sb.toString();
		}
		sb.append(mobileNo.subSequence(0, 3));
		for (int i = 3; i < mobileNo.length() - 4; i++) {
			sb.append("*");
		}
		sb.append(mobileNo.subSequence(mobileNo.length() - 4, mobileNo.length()));
		return sb.toString();
	}

	public static String escape(String value) {
		for (String key : escapeMap.keySet()) {
			value = StringUtils.replace(value, key, escapeMap.get(key));
		}
		return value;
	}

	enum Type {
		forLog, forShow
	}
	
}
