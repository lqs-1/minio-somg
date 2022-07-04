## Springboot整合minio的操作

> [`文档地址`](https://docs.min.io/docs/java-client-quickstart-guide.html)

> 在创建好springboot的项目之后,添加minio的maven坐标
```xml
<dependency>
    <groupId>io.minio</groupId>
    <artifactId>minio</artifactId>
    <version>8.3.3</version>
</dependency>
```
> 在java代码中创建MinioUtils类,这里可以参考[`这篇博客`](https://www.cnblogs.com/wffzk/p/15961335.html)
---
> 新建MinIoUtils类
- 准备工作1,添加相关配置(名字随便取)
```properties
minio.user.endpoint=http://127.0.0.1:9001 # 这个是自己搭建的minio的客户端接口地址
minio.user.username=admin # 这个是用户名
minio.user.password=password # 这个是密码
minio.user.bucket=somg # 这个是buket名称
```
-准备工作2,将配置导入配置对象
```java
package com.example.minio.pojo;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author 李奇凇
 * @date 2022年07月04日 上午11:57
 * @do Minio配置对象
 */

@Data
@Component
@ConfigurationProperties(prefix = "minio.user")
public class Minio {

    private String endpoint;
    private String username;
    private String password;
    private String bucket;
    
}
```
> 核心步骤,在MinIoUtils里面添加工具方法
```java
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
    @SneakyThrows(Exception.class)
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
```
> 可以参考大佬的配置,下面的是老版本的,现在不实用,能看懂就行
```java
import cn.hutool.core.date.DateUtil;
import io.minio.MinioClient;
import io.minio.PutObjectOptions;
import io.minio.messages.Bucket;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
 
import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
 
/**
 * Minio工具类
 */
@Slf4j
@Component
public class MinIoUtil {
 
    public static MinioClient minioClient;
 
    public static ParamConfig paramConfig;
 
    /**
     * 初始化minio配置
     */
    @PostConstruct
    public void init() {
        try {
            log.info("Minio Initialize........................");
            minioClient = MinioClient.builder().endpoint(paramConfig.endpoint).credentials(paramConfig.accessKey, paramConfig.secretKey).build();
            createBucket(paramConfig.bucketName);
            log.info("Minio Initialize........................successful");
        } catch (Exception e) {
            e.printStackTrace();
            log.error("初始化minio配置异常: 【{}】", e.fillInStackTrace());
        }
    }
 
    /**
     * 判断bucket是否存在
     */
    @SneakyThrows(Exception.class)
    public static boolean bucketExists(String bucketName) {
        return minioClient.bucketExists(bucketName);
    }
 
    /**
     * 创建bucket
     */
    @SneakyThrows(Exception.class)
    public static void createBucket(String bucketName) {
        boolean isExist = minioClient.bucketExists(bucketName);
        if (!isExist) {
            minioClient.makeBucket(bucketName);
        }
    }
 
    /**
     * 获取全部bucket
     */
    @SneakyThrows(Exception.class)
    public static List<Bucket> getAllBuckets() {
        return minioClient.listBuckets();
    }
 
    /**
     * 文件上传
     *
     * @param bucketName: 桶名
     * @param fileName:   文件名
     * @param filePath:   文件路径
     */
    @SneakyThrows(Exception.class)
    public static void upload(String bucketName, String fileName, String filePath) {
        minioClient.putObject(bucketName, fileName, filePath, null);
    }
 
    /**
     * 上传文件
     * 返回可以直接预览文件的URL
     */
    public static String uploadFile(MultipartFile file) {
        try {
            //如果存储桶不存在则创建
            if (!bucketExists(ParamConfig.bucketName)) {
                createBucket(ParamConfig.bucketName);
            }
            PutObjectOptions putObjectOptions = new PutObjectOptions(file.getInputStream().available(), -1);
            putObjectOptions.setContentType(file.getContentType());
            String originalFilename = file.getOriginalFilename();
            //得到文件流
            InputStream inputStream = file.getInputStream();
            //保证文件不重名(并且没有特殊字符)
            String fileName = ParamConfig.bucketName + DateUtil.format(new Date(), "_yyyyMMddHHmmss") + originalFilename;
            minioClient.putObject(ParamConfig.bucketName, fileName, inputStream, putObjectOptions);
            return getPreviewFileUrl(ParamConfig.bucketName, fileName);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
 
    /**
     * 文件上传
     * 返回下载文件url地址 和下面upload方法仅传参不同
     * bucketName 也可以直接从ParamConfig对象中获取
     */
    @SneakyThrows(Exception.class)
    public static String upload(String bucketName, String fileName, InputStream stream) {
        minioClient.putObject(bucketName, fileName, stream, new PutObjectOptions(stream.available(), -1));
        return getPreviewFileUrl(bucketName, fileName);
    }
 
    /**
     * 文件上传
     * 返回下载文件url地址  和上面upload方法仅传参不同
     */
    @SneakyThrows(Exception.class)
    public static String upload(String bucketName, MultipartFile file) {
        final InputStream is = file.getInputStream();
        final String fileName = file.getOriginalFilename();
        minioClient.putObject(bucketName, fileName, is, new PutObjectOptions(is.available(), -1));
        is.close();
        return getPreviewFileUrl(bucketName, fileName);
    }
 
    /**
     * 删除文件
     *
     * @param bucketName: 桶名
     * @param fileName:   文件名
     */
    @SneakyThrows(Exception.class)
    public static void deleteFile(String bucketName, String fileName) {
        minioClient.removeObject(bucketName, fileName);
    }
 
    /**
     * 下载文件
     */
//    @SneakyThrows(Exception.class)
//    public static void download(String bucketName, String fileName, HttpServletResponse response) {
//        // 获取对象的元数据
//        final ObjectStat stat = minioClient.statObject(bucketName, fileName);
//        response.setContentType(stat.contentType());
//        response.setCharacterEncoding("UTF-8");
//        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
//        InputStream is = minioClient.getObject(bucketName, fileName);
//        IOUtils.copy(is, response.getOutputStream());
//        is.close();
//    }
 
    /**
     * 获取minio文件的下载或者预览地址
     * 取决于调用本方法的方法中的PutObjectOptions对象有没有设置contentType
     *
     * @param bucketName: 桶名
     * @param fileName:   文件名
     */
    @SneakyThrows(Exception.class)
    public static String getPreviewFileUrl(String bucketName, String fileName) {
        return minioClient.presignedGetObject(bucketName, fileName);
    }
 
}
```

### @SneakyThrows注解详解
> 这个注解就是抛出异常的,帮我们抛出异常
---
> 在我们平时写代码的时候,经常会有异常处理,要么抛出去,要么抓一下,try  catch就用的非常的多没,非常的繁琐
有了@SneakyThrows注解之后,我们就不用自己写try  catch了,因为他会帮我们生成对应的try catch代码
---
>@SneakyThrows不带参数, 默认抛出的就是最大的异常Throwable,如果带了参数,这个参数也只能是异常类型,那么抛出的就是指定的异常
