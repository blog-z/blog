package com.user.utils;

import com.user.entity.User;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;

public class JsonUtil {
    private static Logger logger= LoggerFactory.getLogger(JsonUtil.class);

    private static ObjectMapper objectMapper=new ObjectMapper();

    static {

        objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.ALWAYS);
        objectMapper.configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS,false);
        objectMapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS,false);
        objectMapper.setDateFormat(new SimpleDateFormat(DateTimeUtil.STANDARD_FORMAT));
        objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES,false);

    }

    public static <T> String objToString(T obj){
        if (obj==null){
            return null;
        }
        try{
            return obj instanceof String?(String) obj:objectMapper.writeValueAsString(obj);
        }catch (Exception e){
            logger.info("Parse Object to String error",e);
            return null;
        }
    }

    public static <T> String objToStringPretty(T obj){
        if (obj==null){
            return null;
        }
        try {
            return obj instanceof String ? (String)obj : objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        }catch (Exception e){
            logger.info("Parse Object to String error",e);
            return null;
        }
    }

    public static <T> T stringToObj(String str,Class<T> clazz){
        if (StringUtils.isEmpty(str)||clazz==null){
            return null;
        }
        try{
            return clazz.equals(String.class) ? (T)str : objectMapper.readValue(str,clazz);
        }catch (Exception e){
            logger.info("Parse Object to String error",e);
            return null;
        }
    }

    public static <T> T stringToObj(String str, TypeReference<T> typeReference){
        if (StringUtils.isEmpty(str)){
            return null;
        }
        try{
            return (T) (typeReference.getType().equals(str.getClass())?str:objectMapper.readValue(str,typeReference));
        }catch (Exception e){
            logger.info("Parse String to Object error",e);
            return null;
        }
    }


}
