package com.austain.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "aliyun.oss")
public class OssProperties {
    /**
     * 形如 oss-cn-beijing.aliyuncs.com
     */
    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;
    /**
     * 访问域名，如 https://bucket.oss-cn-beijing.aliyuncs.com
     */
    private String baseUrl;
    /**
     * 上传目录前缀，末尾需保留 /
     */
    private String uploadDir = "avatars/";
}
