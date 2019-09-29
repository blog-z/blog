package com.user.service;

import com.dubbo.commons.ImgResultDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ArticleService {

    ImgResultDto uploadImage(List<MultipartFile> list, int commodityId);
}
