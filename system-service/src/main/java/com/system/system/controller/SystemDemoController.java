package com.system.system.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系统服务冒烟接口，用于验证注册、路由和启动链路。
 */
@RestController
@RequestMapping("/system/demo")
@Tag(name = "服务检测模块", description = "提供系统服务注册和网关路由的连通性检测接口")
public class SystemDemoController {

    @Operation(summary = "检测系统服务", description = "返回系统服务名称和pong消息，用于验证服务链路")
    @GetMapping("/ping")
    public Map<String, String> ping() {
        Map<String, String> data = new LinkedHashMap<>();
        data.put("serviceName", "system-service");
        data.put("message", "pong");
        return data;
    }
}
