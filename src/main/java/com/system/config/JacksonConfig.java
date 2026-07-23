package com.system.config;

import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 全局统一时间格式化（彻底解决时间格式混乱）
 * 支持 LocalDateTime
 * 同application.yaml 中的jackson模块一样
 */
@Configuration
public class JacksonConfig {

    private static final String PATTERN = "yyyy-MM-dd HH:mm:ss";

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        return builder -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(PATTERN);
            // 序列化
            builder.serializerByType(LocalDateTime.class,
                    new LocalDateTimeSerializer(formatter));
            // 反序列化
            builder.deserializerByType(LocalDateTime.class,
                    new LocalDateTimeDeserializer(formatter));
        };
    }
}