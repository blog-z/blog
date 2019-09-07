package com.user.service.impl;

import com.user.commons.ImgResultDto;
import com.user.service.AddCommodityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Date;
import java.util.List;

@Service(value = "addCommodityService")
public class AddCommodityServiceImpl implements AddCommodityService {

    private static final Logger logger= LoggerFactory.getLogger(AddCommodityServiceImpl.class);
    /**
     * 图片上传处理
     */
    @Override
    public ImgResultDto upLoadEditorImg(List<MultipartFile> list, String UploadAbsolutePath, String UploadRelativePath, int commodityId) {


        //工程绝对路径
        String imgUploadAbsolutePath = UploadAbsolutePath;
        //工程相对路径
        String imgUploadRelativePath = UploadRelativePath;
        logger.debug("files.length = " + list.size());
        ImgResultDto imgResultDto = new ImgResultDto();
        //这里定
        String[] urlData = new String[5];
        int index = 0;
        try {
            for (MultipartFile img : list) {
                //获取原始文件名，比如你上传的是　图片．jpg,则fileName＝图片．jpg
                String fileName = img.getOriginalFilename();
                if (fileName == "") {
                    continue;
                }
                logger.debug("file  name = " + fileName);
                String finalPath = imgUploadAbsolutePath + imgUploadRelativePath;  //绝对路径　＋　相对路径
                //为了保证文件名不一致，因此文件名称使用当前的时间戳和4位的随机数，还有原始文件名组成
                //觉得实际的企业开发不应当使用原始文件名，否则上传者使用一些不好的名字，对于下载者就不好了．
                //这里为了调试方便，可以加上．
                String finalFileName = (new Date().getTime()) + Math.round(Math.random() * 1000)  //文件名动态部分
                        + fileName; //文件名　原始文件名
                File newFile = new File(finalPath + finalFileName);
                logger.debug("创建文件夹　= " + newFile.mkdirs() + "  path = " + newFile.getPath());
                logger.debug("" + newFile.getAbsolutePath());
                //保存文件到本地
                img.transferTo(newFile);
                logger.debug("上传图片成功");
                //这里的路径是项目路径＋文件路径＋文件名称
                //这么写不是规范的做法，项目路径应是放在前端处理，只需要发送相对路径和文件名称即可，项目路径由前端加上．
                urlData[index++] = "http://localhost:8080/lanmei-cms/" + imgUploadRelativePath + finalFileName;
                logger.debug("index = " + index
                        + "  url = " + urlData[0]);
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