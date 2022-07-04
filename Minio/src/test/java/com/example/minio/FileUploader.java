package com.example.minio;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.UploadObjectArgs;
import io.minio.errors.MinioException;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * @author 李奇凇
 * @date 2022年07月03日 下午5:51
 * @do minio文件上传demo
 */
public class FileUploader {
    public static void haha()
            throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        try {
            // Create a minioClient with the MinIO server playground, its access key and secret key.
            // 创建一个minio客户端
            MinioClient minioClient =
                    MinioClient.builder()
                            .endpoint("http://127.0.0.1:9001") // minio的客户端接口
                            .credentials("admin", "password") // 用户名和密码
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
            minioClient.uploadObject(
                    UploadObjectArgs.builder()
                            .bucket("haha")
                            .object("haha-2015.sh") // 制定上传的文件名
                            .filename("/home/lqs/start.sh") // 要上传的本地文件的地址
                            .build());
            System.out.println("upload success");
        } catch (MinioException e) {
            System.out.println("Error occurred: " + e);
            System.out.println("HTTP trace: " + e.httpTrace());
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        haha();
    }
}
