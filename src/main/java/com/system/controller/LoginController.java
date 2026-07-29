package com.system.controller;

import com.system.common.Result;
import com.system.dto.LoginDTO;
import com.system.service.LoginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
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
    public Result<String> login(@Valid @RequestBody LoginDTO loginDTO) {
        String token = loginService.login(loginDTO);
        return Result.success(token);
    }
}
