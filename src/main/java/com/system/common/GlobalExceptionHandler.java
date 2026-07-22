package com.system.common;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    // 捕获所有未知异常
    @ExceptionHandler(Exception.class)
    public Result<?> error(Exception e) {
        return Result.fail("server is error : " + e.getMessage());
    }
}
