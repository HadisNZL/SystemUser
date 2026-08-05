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

/**
 * 当前登录用户信息接口。
 */
@RestController
@RequestMapping("/system/profile")
@Tag(name = "个人中心模块", description = "提供当前登录用户的资料、角色和权限信息接口")
public class SysProfileController {

    @Resource
    private SysProfileService sysProfileService;

    @Operation(summary = "获取当前用户资料", description = "获取当前登录用户的基本资料、角色和权限信息")
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public Result<ProfileVO> getCurrentProfile() {
        ProfileVO profile = sysProfileService.getCurrentProfile();
        return Result.success(profile);
    }
}
