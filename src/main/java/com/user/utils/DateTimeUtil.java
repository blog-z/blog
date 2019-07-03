package com.user.utils;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;

public class DateTimeUtil {
    public static final String STANDARD_FORMAT="yyyy-MM-dd HH:mm:ss";


    //字符串 转 date    从外部传入需要转换的类型 比如：yyyy-MM-dd
    public static Date strToDate(String dateTimeStr, String formatStr){
        DateTimeFormatter dateTimeFormat=DateTimeFormat.forPattern(formatStr);
        DateTime dateTime=dateTimeFormat.parseDateTime(dateTimeStr);
        return dateTime.toDate();
    }

    //date 转 字符串    从外部传入需要转换的类型 比如2019-10-10
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

    public static void main(String[] args) {
        Date date=strToDate("2019-10-10","yyyy-MM-dd");
        String string=dateToString(date,"yyyy-MM-dd");
        System.out.println(date);
        System.out.println(string);
    }
}
