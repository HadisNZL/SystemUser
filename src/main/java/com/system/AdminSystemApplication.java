package com.system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling//开启定时任务
@EnableDiscoveryClient//开启Nacos服务注册发现
@SpringBootApplication
public class AdminSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdminSystemApplication.class, args);
    }

}
