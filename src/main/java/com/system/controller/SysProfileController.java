package com.system.controller;

import com.system.common.Result;
import com.system.service.SysProfileService;
import com.system.vo.ProfileVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "当前用户模块", description = "提供当前登录用户信息接口")
@RestController
@RequestMapping("/sys/profile")
public class SysProfileController {

    @Resource
    private SysProfileService sysProfileService;

    @Operation(summary = "获取当前登录用户信息", description = "返回当前用户、角色和权限标识")
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public Result<ProfileVO> getCurrentProfile() {
        ProfileVO profile = sysProfileService.getCurrentProfile();
        return Result.success(profile);
    }
}
