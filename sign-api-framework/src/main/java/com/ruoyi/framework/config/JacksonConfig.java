package com.ruoyi.framework.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * Jackson配置类
 * 全局解决BigDecimal科学计数法问题
 *
 * @author HayDen
 */
@Configuration
public class JacksonConfig {
    
    @Bean
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        ObjectMapper objectMapper = builder.createXmlMapper(false).build();
        
        // 注册自定义模块
        SimpleModule module = new SimpleModule();
        
        // BigDecimal序列化时使用toPlainString()，避免科学计数法（0E-8、1E+6等）
        module.addSerializer(BigDecimal.class, new JsonSerializer<BigDecimal>() {
            @Override
            public void serialize(BigDecimal value, JsonGenerator gen, SerializerProvider serializers) 
                    throws IOException {
                if (value == null) {
                    gen.writeNull();
                } else {
                    // 使用toPlainString()避免科学计数法，然后作为数字写入
                    gen.writeNumber(value.toPlainString());
                }
            }
        });
        
        objectMapper.registerModule(module);
        return objectMapper;
    }
}
