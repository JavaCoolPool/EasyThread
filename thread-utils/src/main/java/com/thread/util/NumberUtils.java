package com.thread.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
	
public class NumberUtils {

	 public static final int PERCENTAGE_SCALE = 4;
	    public static final int CURRENCY_SCALE = 2;
	    private static String CURRENCY_FORMAT = new DecimalFormatSymbols(Locale.SIMPLIFIED_CHINESE).getCurrencySymbol() + "#,##0.00";
	    private static String PERCENTAGE_FORMAT = new DecimalFormatSymbols(Locale.SIMPLIFIED_CHINESE).getCurrencySymbol() + "#,##0.00%";
	    private static String POINTS_FORMAT = new DecimalFormatSymbols(Locale.SIMPLIFIED_CHINESE).getCurrencySymbol() + "#,##0";

	    public static String formatWithoutCurrency(BigDecimal amount) {
	        if (amount.compareTo(BigDecimal.ZERO) == -1) {
	            amount = BigDecimal.ZERO.subtract(amount);
	            String result = new DecimalFormat(CURRENCY_FORMAT).format(amount);
	            return "-" + result.substring(1, result.length());
	        }
	        String result = new DecimalFormat(CURRENCY_FORMAT).format(amount);
	        return result.substring(1, result.length());
	    }

	    public static String formatPoints(BigDecimal amount) {
	        String result = new DecimalFormat(POINTS_FORMAT).format(amount);
	        return result.substring(1, result.length());
	    }

	    public static String formatPercentage(BigDecimal amount) {
	        String result = new DecimalFormat(PERCENTAGE_FORMAT).format(amount);
	        return result.substring(1, result.length());
	    }
	    
	    public static BigDecimal withPercentageScale(BigDecimal value) {
	        return value.setScale(PERCENTAGE_SCALE, RoundingMode.HALF_UP);
	    }

	    public static BigDecimal divide(Long value1,Long value2){
	        if(value2==null || value2==0){
	            return BigDecimal.ONE;
	        }
	        BigDecimal v1 =new BigDecimal(value1);
	        BigDecimal v2 =new BigDecimal(value2);
	        return v1.divide(v2,CURRENCY_SCALE,RoundingMode.HALF_UP);
	    }

	    public static BigDecimal divide(BigDecimal value1,BigDecimal value2){
	        return value1.divide(value2,CURRENCY_SCALE,RoundingMode.HALF_UP);
	    }

	    public static String formatPercentage(Long value1,Long value2){
	      return   formatPercentage(divide(value1,value2));
	    }

	    public static BigDecimal withCurrencyScale(BigDecimal value) {
	        return value.setScale(CURRENCY_SCALE, RoundingMode.HALF_UP);
	    }
	    
	    public static boolean isLessThanZero(BigDecimal value) {
	        return value.compareTo(BigDecimal.ZERO) < 0;
	    }
	    
	    public static boolean isGreaterThanZero(BigDecimal value) {
	    	return value.compareTo(BigDecimal.ZERO) > 0;
	    }

	    public static boolean isEqualsToZero(BigDecimal value) {
	        return value.compareTo(BigDecimal.ZERO) == 0;
	    }

	    public static boolean isLessOrEqualThanZero(BigDecimal value) {
	        return value.compareTo(BigDecimal.ZERO) <= 0;
	    }

	    public static boolean isGreaterOrEqualThan(BigDecimal value1, BigDecimal value2) {
	        return value1.compareTo(value2) >= 0;
	    }

	    public static boolean isGreaterThan(BigDecimal value1, BigDecimal value2) {
	        return value1.compareTo(value2) > 0;
	    }

	    public static <T extends Number> boolean isGreaterOrEqualThan(T  value1, T value2) {
	        if(value1 == null && value2 == null){
	                return true;
	        }else if(value1 == null){
	                return false;
	        }else if(value2 == null){
	            return true;
	        }else{
	            return value1.intValue()>=value2.intValue();
	        }
	    }

	    public static BigDecimal add(BigDecimal value1,BigDecimal value2){
	    	if(value1==null){
	    		value1 = BigDecimal.ZERO;
	    	}
	    	if(value2==null){
	    		value2 = BigDecimal.ZERO;
	    	}
	    	return value1.add(value2);
	    }

	    public static BigDecimal subtract(BigDecimal value1,BigDecimal value2){
	    	if(value1==null){
	    		value1 = BigDecimal.ZERO;
	    	}
	    	if(value2==null){
	    		value2 = BigDecimal.ZERO;
	    	}
	    	return value1.subtract(value2);
	    }
	
}
