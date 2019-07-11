package com.user.utils;

import com.user.config.ElasticsearchConfig;
import com.user.entity.User;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;

public class ElasticsearchUtil {
        private static final Logger logger = LoggerFactory.getLogger(ElasticsearchUtil.class);

//        private static Request request=new Request("PUT","/blog/user/");
        //添加索引
        public static void insertUser(String IndexName, String TypeName, String Id, User user) {
                String userJson="{" +
                        "\"userId\":\"6\"," +
                        "\"userName\":\"aaaa\"," +
                        "\"userEmail\":\"2222@qq.com\"," +
                        "\"userPhone\":\"8888\"," +
                        "\"userPassword\":\"247234123948\"," +
                        "\"userRole\":\"1\"," +
                        "\"userQuestion\":\"asljfl\"," +
                        "\"userAnswer\":\"klashfjks\"," +
                        "\"createTime\":\"2019-07-04 08:33:27\"," +
                        "\"updateTime\":\"2019-07-04 08:33:27\"" +
                        "}";



                // JSON格式字符串
                HttpEntity entity = new NStringEntity(userJson, ContentType.APPLICATION_JSON);
                try {
                        ElasticsearchConfig.getLowRestClient().performRequest("PUT", "/" + IndexName +"/"+ TypeName +"/"+ Id+"/", Collections.EMPTY_MAP,entity);
                } catch (IOException e) {
                        logger.info("插入数据失败！");
                        e.printStackTrace();
                }
        }


}
