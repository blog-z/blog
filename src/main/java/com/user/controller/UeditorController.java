package com.user.controller;

import com.baidu.ueditor.ActionEnter;
import com.sun.jersey.api.client.Client;
import com.user.commons.ServerResponse;
import com.user.utils.ResponseUtil;
import com.user.utils.UploadUtil;
import org.apache.commons.io.FilenameUtils;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Map;

@RestController
@RequestMapping(value = "upload")
public class UeditorController {

    //图片上传服务器地址
    private static final String ueditorHost="http://127.0.0.1:8080";

    //文件保存地址
    private static final String ueditor="/usr/local/nginx/baidu/";

    @ResponseBody
    @RequestMapping(value="/ueditorUpload.do", method={RequestMethod.GET, RequestMethod.POST})
    public void editorUpload(HttpServletRequest request, HttpServletResponse response, String action) {
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json");
        String rootPath = request.getSession().getServletContext().getRealPath("/");

        try {
            if("config".equals(action)){    //如果是初始化
                String exec = new ActionEnter(request, rootPath).exec();
                PrintWriter writer = response.getWriter();
                writer.write(exec);
                writer.flush();
                writer.close();
            }else if("uploadimage".equals(action) || "uploadvideo".equals(action) || "uploadfile".equals(action)){    //如果是上传图片、视频、和其他文件
                try {
                    MultipartResolver resolver = new CommonsMultipartResolver(request.getSession().getServletContext());
                    MultipartHttpServletRequest Murequest = resolver.resolveMultipart(request);
                    Map<String, MultipartFile> files = Murequest.getFileMap();//得到文件map对象
                    // 实例化一个jersey
                    Client client = new Client();

                    for(MultipartFile pic: files.values()){
                        JSONObject jo = new JSONObject();
                        long size = pic.getSize();    //文件大小
                        String originalFilename = pic.getOriginalFilename();  //原来的文件名
                        String uploadInfo = UploadUtil.upload(client, pic, request, response, ueditorHost, ueditor);
                        if(!"".equals(uploadInfo)){    //如果上传成功
                            String[] infoList = uploadInfo.split(";");
                            jo.put("state", "SUCCESS");
                            jo.put("original", originalFilename);//原来的文件名
                            jo.put("size", size);//文件大小
                            jo.put("title", infoList[1]);  //随意，代表的是鼠标经过图片时显示的文字
                            jo.put("type", FilenameUtils.getExtension(pic.getOriginalFilename()));  //文件后缀名
                            jo.put("url", infoList[2]);//这里的url字段表示的是上传后的图片在图片服务器的完整地址（http://ip:端口/***/***/***.jpg）
                        }else{    //如果上传失败
                        }
//                        ResponseUtils.renderJson(response, jo.toString());
                        ResponseUtil.out(response,ServerResponse.createBySuccess(jo));
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
        }
    }
}
