package com.system.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 全局跨域配置（项目统一配置，不用单个注解）
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                // 允许所有来源
                .allowedOriginPatterns("*")
                // 允许全部请求方式
                .allowedMethods("*")
//                .allowedMethods("GET","POST","PUT","DELETE")
                // 允许携带cookie
                .allowCredentials(true)
                // 最大跨域缓存时长
                .maxAge(3600)
                .allowedHeaders("*");
    }
}
