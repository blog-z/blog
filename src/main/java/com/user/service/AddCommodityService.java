package com.user.service;

import com.dubbo.commons.ImgResultDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AddCommodityService {

    /**
     * 上传商品图片
     */
    ImgResultDto upLoadEditorImg(List<MultipartFile> list, String UploadAbsolutePath, String UploadRelativePath, int commodityId);
}
