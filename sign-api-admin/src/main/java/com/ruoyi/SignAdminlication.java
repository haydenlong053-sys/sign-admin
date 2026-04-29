package com.ruoyi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;

/**
 * 启动程序
 *
 * @author HayDen
 */
@SpringBootApplication(exclude = {
        DataSourceAutoConfiguration.class,
        DruidDataSourceAutoConfigure.class
})
public class SignAdminlication
{
    public static void main(String[] args)
    {
        SpringApplication.run(SignAdminlication.class, args);
        System.out.println("(♥◠‿◠)ﾉﾞ  Sign管理系统  ----1-- ლ(´ڡ`ლ)ﾞ--->http://127.0.0.1:8089");
    }
}