package com.system.auth;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 认证服务启动类，当前用于演示服务间调用。
 */
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
@OpenAPIDefinition(
        info = @Info(
                title = "认证服务接口",
                description = "文档包含认证、系统、文件、日志四个服务分组，请通过左上角下拉框切换。",
                version = "1.0"
        ),
        servers = @Server(url = "/", description = "网关统一入口")
)
public class AuthServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }
}
