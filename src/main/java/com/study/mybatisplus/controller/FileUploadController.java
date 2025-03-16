package com.study.mybatisplus.controller;

import com.study.mybatisplus.domain.Result;
import com.study.mybatisplus.utils.AliOssUtil;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
@RestController
public class FileUploadController {
    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file) throws Exception {
        String originalFileName=file.getOriginalFilename();
        String filename= UUID.randomUUID().toString()+originalFileName.substring(originalFileName.lastIndexOf("."));
        //file.transferTo(new File("D:\\Apipost" + originalFileName));
        String url= AliOssUtil.uploadFile(filename,file.getInputStream());
        return Result.success(url);
    }
}
