package com.user.utils;

import com.dubbo.commons.ServerResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class ResponseUtil {

    private static Logger logger= LoggerFactory.getLogger(ResponseUtil.class);


    private static PrintWriter printWriter;

    public static void out(ServletResponse response, ServerResponse serverResponse){
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        try {
            printWriter=response.getWriter();
        } catch (IOException e) {
            logger.info("示例化 printWriter 失败");
        }
        try {
            printWriter.print(JsonUtil.objToString(serverResponse));
        } finally {
            if (printWriter!=null){
                printWriter.flush();
                printWriter.close();
            }
        }
    }
}
