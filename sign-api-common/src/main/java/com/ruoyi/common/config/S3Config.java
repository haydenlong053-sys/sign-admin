package com.ruoyi.common.config;

import com.ruoyi.common.constant.S3Properties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProviderChain;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.InstanceProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

/**
 * S3 配置类
 * <p>
 * 凭证顺序：先 EC2 实例元数据（实例角色，与宿主机 aws cli 一致），再 DefaultCredentialsProvider。
 * 避免 Docker/进程里误配的 {@code AWS_ACCESS_KEY_ID} 优先于实例角色导致 S3 403。
 *
 * @author hayden
 */
@Configuration
@ConditionalOnProperty(prefix = "aws.s3", name = "region")
public class S3Config {

    private static AwsCredentialsProvider s3CredentialsProvider()
    {
        return AwsCredentialsProviderChain.builder()
                .addCredentialsProvider(InstanceProfileCredentialsProvider.create())
                .addCredentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }

    /**
     * 创建 S3Client Bean（用于直接上传文件到 S3）
     */
    @Bean
    public S3Client s3Client(S3Properties props) {
        return S3Client.builder()
                .region(Region.of(props.getRegion()))
                .credentialsProvider(s3CredentialsProvider())
                .build();
    }

    /**
     * 创建 S3Presigner Bean（保留用于生成预签名URL的场景）
     */
    @Bean
    public S3Presigner s3Presigner(S3Properties props) {
        return S3Presigner.builder()
                .region(Region.of(props.getRegion()))
                .credentialsProvider(s3CredentialsProvider())
                .build();
    }
}