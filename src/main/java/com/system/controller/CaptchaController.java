package com.system.controller;

import com.system.annotation.RateLimit;
import com.system.common.Result;
import com.system.service.CaptchaService;
import com.system.vo.CaptchaVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "验证码", description = "提供登录验证码接口")
@RestController
public class CaptchaController {

    @Resource
    private CaptchaService captchaService;

    @Operation(summary = "获取验证码", description = "返回验证码key和Base64图片")
    @GetMapping("/captcha")
    @RateLimit(seconds = 60, maxCount = 10, key = "captcha")
    public Result<CaptchaVO> getCaptcha() {
        CaptchaVO captcha = captchaService.generateCaptcha();
        return Result.success(captcha);
    }
}
