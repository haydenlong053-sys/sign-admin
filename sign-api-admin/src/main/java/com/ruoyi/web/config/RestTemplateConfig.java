package com.ruoyi.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * RestTemplate配置类
 * 
 * @author HayDen
 * @date 2025-01-15
 */
@Configuration
public class RestTemplateConfig
{
    @Bean
    public RestTemplate restTemplate()
    {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(10000); // 连接超时10秒
        factory.setReadTimeout(30000);    // 读取超时30秒
        return new RestTemplate(factory);
    }
}

