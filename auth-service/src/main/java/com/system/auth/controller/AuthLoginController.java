package com.system.auth.controller;

import com.system.auth.dto.AuthLoginDTO;
import com.system.auth.service.AuthCaptchaService;
import com.system.auth.service.AuthLoginService;
import com.system.auth.service.AuthLogoutService;
import com.system.auth.service.AuthRateLimitService;
import com.system.auth.vo.CaptchaVO;
import com.system.common.BusinessException;
import com.system.common.Result;
import com.system.common.ResultCode;
import com.system.common.SystemConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证服务登录接口。
 */
@RestController
@RequestMapping("/auth")
@Tag(name = "认证管理模块", description = "提供验证码、用户登录和退出登录接口")
public class AuthLoginController {

    private final AuthLoginService authLoginService;
    private final AuthCaptchaService authCaptchaService;
    private final AuthLogoutService authLogoutService;
    private final AuthRateLimitService authRateLimitService;

    public AuthLoginController(AuthLoginService authLoginService,
                               AuthCaptchaService authCaptchaService,
                               AuthLogoutService authLogoutService,
                               AuthRateLimitService authRateLimitService) {
        this.authLoginService = authLoginService;
        this.authCaptchaService = authCaptchaService;
        this.authLogoutService = authLogoutService;
        this.authRateLimitService = authRateLimitService;
    }

    @Operation(summary = "获取图形验证码", description = "生成验证码图片及一次性验证码标识")
    @GetMapping("/captcha")
    public Result<CaptchaVO> captcha(HttpServletRequest request) {
        authRateLimitService.checkLimit(buildRateLimitKey("captcha", request), 60, 10);
        return Result.success(authCaptchaService.generateCaptcha());
    }

    @Operation(summary = "用户登录", description = "校验验证码和账号密码，成功后签发JWT")
    @PostMapping("/login")
    public Result<String> login(@Valid @RequestBody AuthLoginDTO loginDTO, HttpServletRequest request) {
        authRateLimitService.checkLimit(buildRateLimitKey("login", request), 60, 5);
        return Result.success(authLoginService.login(loginDTO));
    }

    @Operation(summary = "退出登录", description = "将当前JWT加入黑名单，使其在剩余有效期内不可继续使用")
    @PostMapping("/logout")
    public Result<Boolean> logout(HttpServletRequest request) {
        authLogoutService.logout(getToken(request));
        return Result.success(true);
    }

    private String getToken(HttpServletRequest request) {
        String authHeader = request.getHeader(SystemConstants.AUTHORIZATION_HEADER);
        if (authHeader == null || !authHeader.startsWith(SystemConstants.BEARER_PREFIX)) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        return authHeader.substring(SystemConstants.BEARER_PREFIX_LENGTH);
    }

    private String buildRateLimitKey(String action, HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        String ip = forwardedFor == null || forwardedFor.isBlank()
                ? request.getRemoteAddr()
                : forwardedFor.split(",")[0].trim();
        return action + ":" + ip;
    }
}
