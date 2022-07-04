package com.example.minio.controller;

import com.example.minio.utils.MinioUtils;
import com.example.minio.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author 李奇凇
 * @date 2022年07月03日 下午6:14
 * @do Minio文件操作测试
 */
@RestController
@RequestMapping("file")
public class FileController {

    @Autowired
    MinioUtils minioUtils;


    @PostMapping("uploadFile")
    public R uploadFile(@RequestBody MultipartFile file) throws IOException {

        InputStream inputStream = file.getInputStream();
        String originalFilename = file.getOriginalFilename();

        String contentType = file.getContentType();

        R result = minioUtils.uploadFile(originalFilename, inputStream, contentType);

        return result;
    }

    @GetMapping("downloadFile/{objectName}")
    public void downloadFile(@PathVariable("objectName") String objectName, HttpServletResponse response) {
        minioUtils.downFile(objectName, response);
    }


    @DeleteMapping("deleteFile/{objectName}")
    public R deleteFile(@PathVariable("objectName") String objectName){
        R result = minioUtils.deleteFile(objectName);
        return result;
    }


}
