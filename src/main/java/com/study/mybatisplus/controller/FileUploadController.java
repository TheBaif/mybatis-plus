package com.study.mybatisplus.controller;

import com.study.mybatisplus.domain.Result;
import com.study.mybatisplus.utils.AliOssUtil;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
@RestController
public class FileUploadController {
    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file, @RequestParam(required = false) String directory) throws Exception {
        String originalFileName = file.getOriginalFilename();
        String filename;

        // Add directory prefix if provided
        if (directory != null && !directory.isEmpty()) {
            filename = directory + "/" + UUID.randomUUID().toString() + originalFileName.substring(originalFileName.lastIndexOf("."));
        } else {
            filename = UUID.randomUUID().toString() + originalFileName.substring(originalFileName.lastIndexOf("."));
        }

        String url = AliOssUtil.uploadFile(filename, file.getInputStream());
        return Result.success(url);
    }
}