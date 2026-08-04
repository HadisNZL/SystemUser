package com.system.auth.controller;

import com.system.auth.client.AdminSystemDemoClient;
import com.system.auth.vo.AuthDemoVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * OpenFeign服务调用演示接口。
 */
@RestController
@RequestMapping("/auth/demo")
public class AuthDemoController {

    private final AdminSystemDemoClient adminSystemDemoClient;

    public AuthDemoController(AdminSystemDemoClient adminSystemDemoClient) {
        this.adminSystemDemoClient = adminSystemDemoClient;
    }

    @GetMapping("/ping")
    public AuthDemoVO ping() {
        return new AuthDemoVO("auth-service", adminSystemDemoClient.ping());
    }
}
