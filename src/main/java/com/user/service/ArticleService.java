package com.user.service;

import com.user.commons.ImgResultDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ArticleService {

    public ImgResultDto uploadImage(List<MultipartFile> list, String UploadAbsolutePath, String UploadRelativePath, int commodityId);
}
