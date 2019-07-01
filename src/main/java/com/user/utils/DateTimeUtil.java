package com.user.utils;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;

public class DateTimeUtil {
    public static final String STANDARD_FORMAT="yyyy-MM-dd HH:mm:ss";

    public static Date strToDate(String dateTimeStr, String formatStr){
        DateTimeFormatter dateTimeFormat=DateTimeFormat.forPattern(formatStr);
        DateTime dateTime=dateTimeFormat.parseDateTime(dateTimeStr);
        return dateTime.toDate();
    }

    public static String dateToString(Date date,String formater){
        DateTime dateTime=new DateTime(date);
        return dateTime.toString(formater);
    }

    public static Date strToDate(String formatStr){
        DateTimeFormatter dateTimeFormat=DateTimeFormat.forPattern(STANDARD_FORMAT);
        DateTime dateTime=dateTimeFormat.parseDateTime(formatStr);
        return dateTime.toDate();
    }

    public static String dateToString(Date date){
        DateTime dateTime=new DateTime(date);
        return dateTime.toString(STANDARD_FORMAT);
    }
}
