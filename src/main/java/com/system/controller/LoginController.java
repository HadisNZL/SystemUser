package com.system.controller;

import com.system.common.BusinessException;
import com.system.common.Result;
import com.system.common.ResultCode;
import com.system.common.SystemConstants;
import com.system.annotation.RateLimit;
import com.system.dto.LoginDTO;
import com.system.service.LoginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "登录", description = "提供用户的增删改查及并发控制接口")
@RestController
@RequestMapping("/sys/")
public class LoginController {

    @Resource
    private LoginService loginService;

    @PostMapping("/login")
    @Operation(summary = "用户登录")
    @RateLimit(seconds = 60, maxCount = 5, key = "login")
    public Result<String> login(@Valid @RequestBody LoginDTO loginDTO) {
        String token = loginService.login(loginDTO);
        return Result.success(token);
    }

    @PostMapping("/logout")
    @Operation(summary = "退出登录")
    @PreAuthorize("isAuthenticated()")
    public Result<Boolean> logout(HttpServletRequest request) {
        loginService.logout(getToken(request));
        return Result.success(true);
    }

    private String getToken(HttpServletRequest request) {
        String authHeader = request.getHeader(SystemConstants.AUTHORIZATION_HEADER);
        if (authHeader == null || !authHeader.startsWith(SystemConstants.BEARER_PREFIX)) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        return authHeader.substring(SystemConstants.BEARER_PREFIX_LENGTH);
    }
}
