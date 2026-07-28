package com.system.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Knife4j配置
 */
@Configuration
public class Knife4jConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI().info(new Info().title("企业级后端接口文档") // 文档标题
                .version("1.0.0")                  // 版本号
                .description("基于 Spring Boot 3 与 Knife4j 构建的接口文档") // 描述
                .contact(new Contact().name("开发者")         // 作者姓名
                        .email("your-email@example.com")));
    }
}