package com.system.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * 文件服务MyBatis配置。
 */
@Configuration
@MapperScan("com.system.mapper")
public class MyBatisPlusConfig {
}
