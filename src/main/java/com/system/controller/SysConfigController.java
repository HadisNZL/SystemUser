package com.system.controller;

import com.system.common.Result;
import com.system.config.prop.SystemInfoProperties;
import com.system.vo.SystemInfoVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系统配置接口，用于验证配置中心读取结果。
 */
@Tag(name = "系统配置模块", description = "提供系统配置读取接口")
@RestController
@RequestMapping("/sys/config")
public class SysConfigController {

    @Resource
    private SystemInfoProperties systemInfoProperties;

    @Operation(summary = "获取系统展示信息", description = "读取本地或Nacos配置中心中的系统展示信息")
    @GetMapping("/info")
    @PreAuthorize("isAuthenticated()")
    public Result<SystemInfoVO> getSystemInfo() {
        SystemInfoVO vo = new SystemInfoVO();
        vo.setName(systemInfoProperties.getName());
        vo.setDescription(systemInfoProperties.getDescription());
        vo.setVersion(systemInfoProperties.getVersion());
        return Result.success(vo);
    }
}
