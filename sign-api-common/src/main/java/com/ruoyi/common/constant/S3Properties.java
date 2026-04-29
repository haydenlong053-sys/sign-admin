package com.ruoyi.common.constant;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import com.ruoyi.common.utils.StringUtils;

/**
 * @author hayden
 */
@Component
@ConfigurationProperties(prefix = "aws.s3")
public class S3Properties {
    private String region;
    private String bucket;
    /** 对外访问基址（如 CloudFront），不含末尾斜杠，如 https://d8md8haqjykfk.cloudfront.net */
    private String publicBaseUrl;
    private String accessKey;
    private String secretKey;

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getPublicBaseUrl() {
        return publicBaseUrl;
    }

    public void setPublicBaseUrl(String publicBaseUrl) {
        this.publicBaseUrl = publicBaseUrl;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    /**
     * 对象 key 对应的浏览器访问 URL：配置了 publicBaseUrl 时走 CDN，否则 S3 直链
     */
    public String resolvePublicObjectUrl(String objectKey)
    {
        if (StringUtils.isEmpty(objectKey))
        {
            return null;
        }
        if (StringUtils.isNotEmpty(publicBaseUrl))
        {
            return StringUtils.stripEnd(publicBaseUrl, "/") + "/" + objectKey;
        }
        return "https://" + bucket + ".s3." + region + ".amazonaws.com/" + objectKey;
    }
}