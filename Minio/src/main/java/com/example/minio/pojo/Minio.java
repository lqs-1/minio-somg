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
