package com.system.controller;

import com.system.common.BusinessException;
import com.system.common.Result;
import com.system.common.ResultCode;
import com.system.service.InternalAuthorizationService;
import com.system.vo.InternalUserAuthorizationVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

/**
 * 为下游服务提供内部用户授权查询。
 */
@Validated
@RestController
@RequestMapping("/internal/authorization")
@Tag(name = "内部授权接口", description = "仅供下游服务恢复用户状态和权限上下文")
public class InternalAuthorizationController {

    private static final String INTERNAL_SOURCE_HEADER = "X-Internal-Source";
    private static final Set<String> INTERNAL_SOURCES = Set.of("log-service", "file-service");

    private final InternalAuthorizationService internalAuthorizationService;

    public InternalAuthorizationController(InternalAuthorizationService internalAuthorizationService) {
        this.internalAuthorizationService = internalAuthorizationService;
    }

    @Operation(summary = "查询用户授权信息", description = "供受信任服务通过用户ID查询账号状态和权限标识")
    @GetMapping("/user")
    public Result<InternalUserAuthorizationVO> getUserAuthorization(
            @RequestParam @Min(value = 1, message = "用户ID必须大于等于1") Long userId,
            @RequestHeader(value = INTERNAL_SOURCE_HEADER, required = false) String source) {
        if (source == null || !INTERNAL_SOURCES.contains(source)) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
        return Result.success(internalAuthorizationService.getUserAuthorization(userId));
    }
}
