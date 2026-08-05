package com.system.log;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 操作日志服务启动类。
 */
@EnableScheduling
@EnableFeignClients(basePackages = "com.system.client")
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = "com.system", exclude = UserDetailsServiceAutoConfiguration.class)
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT")
@OpenAPIDefinition(
        info = @Info(
                title = "日志服务接口",
                description = "文档包含认证、系统、文件、日志四个服务分组，请通过左上角下拉框切换。",
                version = "1.0"
        ),
        security = @SecurityRequirement(name = "bearerAuth"),
        servers = @Server(url = "/", description = "网关统一入口")
)
public class LogServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(LogServiceApplication.class, args);
    }
}
