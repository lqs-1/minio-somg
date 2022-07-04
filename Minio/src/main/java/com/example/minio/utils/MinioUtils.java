package com.example.minio.utils;

import com.example.minio.constant.REnum;
import com.example.minio.pojo.Minio;
import io.minio.*;
import io.minio.messages.Bucket;
import lombok.SneakyThrows;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.List;

/**
 * @author 李奇凇
 * @date 2022年07月04日 上午11:44
 * @do Minio工具类
 */

@Component
public class MinioUtils {


    /**
     * 注入配置对象
     */
    @Autowired
    private Minio minio;


    /**
     * 创建客户端
     * @return
     */
    @SneakyThrows(Exception.class)
    public MinioClient createMinioClient(){
        // 创建一个minio客户端
        MinioClient minioClient =
                MinioClient.builder()
                        .endpoint(minio.getEndpoint()) // minio的客户端接口
                        .credentials(minio.getUsername(), minio.getPassword()) // 用户名和密码
                        .build();
        return minioClient;
    }


    /**
     * 获取全部bucket
     */
    @SneakyThrows(Exception.class)
    public List<Bucket> getAllBuckets() {
        return createMinioClient().listBuckets();
    }


    /**
     * 判断bucket是否存在,不存在就创建
     * @param minioClient 客户端对象
     */
    @SneakyThrows(Exception.class)
    public void bucketExist(MinioClient minioClient){
        // 是否存在haha这个bucket
        boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(minio.getBucket()).build());
        if (!found) {
            // 不存在就创建
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(minio.getBucket()).build());
        }
    }


    /**
     * 文件上传
     * @param objectName 对象名称
     * @param objectInputStream 对象的输入流
     * @param contentType 对象的类型
     */
    @SneakyThrows(Exception.class)
    public R uploadFile(String objectName, InputStream objectInputStream, String contentType) {

        // 创建minio客户端
        MinioClient minioClient = createMinioClient();

        // 判断bucket是否存在
        bucketExist(minioClient);

        // 上传文件
        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(minio.getBucket())
                        .object(objectName)
                        .contentType(contentType)
                        .stream(
                                objectInputStream,
                                -1,
                                10485760
                        )
                        .build()
        );
        return R.ok(REnum.UPLOAD_FILE_SUCCESS.getStatusCode(), REnum.UPLOAD_FILE_SUCCESS.getStatusMsg());

    }


    /**
     *  文件下载
     * @param objectName 对象名字
     * @param response httpservletresponse对象
     * @return
     */
    @SneakyThrows({Exception.class})
    public void downFile(String objectName, HttpServletResponse response){
        // 创建minio客户端
        MinioClient minioClient = createMinioClient();

        // 判断bucket是否存在
        bucketExist(minioClient);


        // 获取某个对象的元数据
        StatObjectResponse statObject = minioClient.statObject(
                StatObjectArgs.builder()
                        .bucket(minio.getBucket())
                        .object(objectName)
                        .build()
        );

        // 设置响应格式
        response.setContentType(statObject.contentType());
        // 下载文件
        InputStream stream = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(minio.getBucket())
                        .object(objectName)
                        .build());
        // 利用工具包写入对象
        IOUtils.copy(stream, response.getOutputStream());
    }


    /**
     * 删除文件
     * @param objectName 对象名字
     * @return
     */

    @SneakyThrows(Exception.class)
    public R deleteFile(String objectName){
        // 创建minio客户端
        MinioClient minioClient = createMinioClient();

        // 判断bucket是否存在
        bucketExist(minioClient);

        // 删除文件
        minioClient.removeObject(RemoveObjectArgs.builder()
                        .bucket(minio.getBucket())
                        .object(objectName)
                .build());
        return R.ok(REnum.DELETE_FILE_SUCCESS.getStatusCode(), REnum.DELETE_FILE_SUCCESS.getStatusMsg());

    }


}
