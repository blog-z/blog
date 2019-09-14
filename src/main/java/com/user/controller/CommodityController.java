package com.user.controller;

import com.dubbo.commons.ImgResultDto;
import com.user.service.AddCommodityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
public class CommodityController {

    @Resource(name = "addCommodityService")
    AddCommodityService addCommodityService;

    private static final Logger logger = LoggerFactory.getLogger("CommodityController.class");

    @RequestMapping(path="/upload/editor/img")
    //RequestParam中的属性名称要和前端定义的一致，上面说明了．所以写"img"
    //使用List<MultipartFile>进行接收
    //返回的是一个Ｄto类，后面会说明，使用@ResponseBody会将其转换为Ｊson格式数据
    public ImgResultDto uploadEditorImg(@RequestParam("img") List<MultipartFile> list, HttpServletRequest request) {
        //这里是我在web中定义的两个初始化属性，保存目录的绝对路径和相对路径，你们可以自定义
        String imgUploadAbsolutePath = request.getServletContext().getInitParameter("imgUploadAbsolutePath");
        String imgUploadRelativePath = request.getServletContext().getInitParameter("imgUploadRelativePath");

        //服务曾处理数据，返回Dto
        ImgResultDto imgResult = addCommodityService.upLoadEditorImg(list, imgUploadAbsolutePath, imgUploadRelativePath,1);
        return imgResult;
    }
}
