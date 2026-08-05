package com.system.controller;

import com.system.common.BusinessException;
import com.system.common.Result;
import com.system.common.ResultCode;
import com.system.service.InternalAuthService;
import com.system.vo.InternalLoginUserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 为认证服务提供内部用户查询接口。
 */
@RestController
@RequestMapping("/internal/auth")
@Tag(name = "内部认证接口", description = "仅供认证服务查询登录用户信息")
public class InternalAuthController {

    private static final String INTERNAL_SOURCE_HEADER = "X-Internal-Source";
    private static final String INTERNAL_SOURCE_VALUE = "auth-service";

    private final InternalAuthService internalAuthService;

    public InternalAuthController(InternalAuthService internalAuthService) {
        this.internalAuthService = internalAuthService;
    }

    @Operation(summary = "查询登录用户", description = "供auth-service通过用户名查询密码、状态和用户ID")
    @GetMapping("/login-user")
    public Result<InternalLoginUserVO> getLoginUser(@RequestParam String username,
                                                    @RequestHeader(value = INTERNAL_SOURCE_HEADER, required = false) String source) {
        if (!INTERNAL_SOURCE_VALUE.equals(source)) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
        return Result.success(internalAuthService.getLoginUser(username));
    }
}
