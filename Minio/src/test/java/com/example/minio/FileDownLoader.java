package com.example.minio;

import io.minio.*;
import io.minio.errors.MinioException;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * @author 李奇凇
 * @date 2022年07月03日 下午6:09
 * @do minio文件下载demo
 */
public class FileDownLoader {

    public static void main(String[] args)
            throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        try {
            // Create a minioClient with the MinIO server playground, its access key and secret key.
            MinioClient minioClient =
                    MinioClient.builder()

                            .endpoint("http://127.0.0.1:9001")
                            .credentials("admin", "password")
                            .build();

            // Make 'asiatrip' bucket if not exist.
            // 是否存在haha这个bucket
            boolean found =
                    minioClient.bucketExists(BucketExistsArgs.builder().bucket("haha").build());
            if (!found) {
                // 不存在就创建
                // Make a new bucket called 'asiatrip'.
                minioClient.makeBucket(MakeBucketArgs.builder().bucket("haha").build());
            }

            // Upload '/home/user/Photos/asiaphotos.zip' as object name 'asiaphotos-2015.zip' to bucket
            // 'asiatrip'.
            // 上传文件
            minioClient.downloadObject(
                DownloadObjectArgs.builder()
                        .bucket("haha")
                        .object("haha-2015.sh")
                        .filename("./haha.sh")
                        .build());
            System.out.println("upload success");
        } catch (MinioException e) {
            System.out.println("Error occurred: " + e);
            System.out.println("HTTP trace: " + e.httpTrace());
        }
    }



}
