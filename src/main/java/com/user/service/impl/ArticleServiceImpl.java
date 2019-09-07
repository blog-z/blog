package com.user.service.impl;

import com.user.commons.ImgResultDto;
import com.user.commons.ServerResponse;
import com.user.service.ArticleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Date;
import java.util.List;

@Service(value = "articleService")
public class ArticleServiceImpl implements ArticleService {

    private static final Logger logger= LoggerFactory.getLogger(ArticleServiceImpl.class);

    //图片保存的主路径(即在nginx静态图片服务器下) /usr/local/nginx/
    private static final String imagePath="/usr/local/nginx/wangEditor-3.1.1/images/";

    /**
     * 上传图片
     */
    public ImgResultDto uploadImage(List<MultipartFile> list, String UploadAbsolutePath, String UploadRelativePath, int commodityId){

        //工程绝对路径
        String imgUploadAbsolutePath = UploadAbsolutePath;
        //工程相对路径
        String imgUploadRelativePath = UploadRelativePath;

        //这是wangEditor富文本编辑器的上传图片的返回值类
        ImgResultDto imgResultDto = new ImgResultDto();

        //这里定
        String[] urlData = new String[5];
        int index = 0;
        try {
            for(MultipartFile img : list) {
            //获取原始文件名，
                String fileName = img.getOriginalFilename();
                if(fileName == "") {
                    continue;
                }
                logger.info("file  name = " + fileName);

                //为了保证文件名不一致，因此文件名称使用当前的时间戳和4位的随机数，还有原始文件名组成
                //觉得实际的企业开发不应当使用原始文件名，否则上传者使用一些不好的名字，对于下载者就不好了．
                //这里为了调试方便，可以加上．
                String finalFileName = new Date().toString(); //文件名　原始文件名
                File newFile = new File( imagePath + finalFileName);
                logger.info("创建文件夹　= " + newFile.mkdirs() +  "  path = " + newFile.getPath());
                logger.info("" + newFile.getAbsolutePath());
                //保存文件到本地
                img.transferTo(newFile);
                logger.info("上传图片成功");
                //持久化到数据库,数据库只保存图片的位置
                logger.info("数据库写入图片成功");
                //这里的路径是项目路径＋文件路径＋文件名称
                //这么写不是规范的做法，项目路径应是放在前端处理，只需要发送相对路径和文件名称即可，项目路径由前端加上．
                urlData[index++] = "http://localhost:8082/images/"+finalFileName;
                logger.info("index = " + index + "  url = " + urlData[0]);
                //设置异常代号
                imgResultDto.setErrno(0);
            }
            imgResultDto.setData(urlData);
            //返回Ｄto
            return imgResultDto;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            return imgResultDto;
        }
    }
}
