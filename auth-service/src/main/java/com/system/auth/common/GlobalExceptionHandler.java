package com.system.auth.common;

import com.system.common.BusinessException;
import com.system.common.Result;
import com.system.common.ResultCode;
import jakarta.validation.ConstraintViolationException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.converter.HttpMessageNotReadableException;

import java.util.stream.Collectors;

/**
 * 认证服务统一异常处理。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public Result<?> businessExceptionHandler(BusinessException e) {
        return Result.fail(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<?> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        return Result.fail(ResultCode.BAD_REQUEST, "参数错误：" + buildFieldErrorMessage(e.getBindingResult().getFieldErrors()));
    }

    @ExceptionHandler(BindException.class)
    public Result<?> bindExceptionHandler(BindException e) {
        return Result.fail(ResultCode.BAD_REQUEST, "参数错误：" + buildFieldErrorMessage(e.getBindingResult().getFieldErrors()));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public Result<?> constraintViolationExceptionHandler(ConstraintViolationException e) {
        String message = e.getConstraintViolations().stream()
                .map(item -> item.getPropertyPath() + "：" + item.getMessage())
                .collect(Collectors.joining("；"));
        return Result.fail(ResultCode.BAD_REQUEST, "参数错误：" + defaultMessage(message));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Result<?> httpMessageNotReadableExceptionHandler(HttpMessageNotReadableException e) {
        return Result.fail(ResultCode.BAD_REQUEST, "参数错误：请求体格式不正确");
    }

    @ExceptionHandler(Exception.class)
    public Result<?> error(Exception e) {
        return Result.fail(ResultCode.SYSTEM_ERROR);
    }

    private String buildFieldErrorMessage(java.util.List<FieldError> fieldErrors) {
        String message = fieldErrors.stream()
                .map(error -> error.getField() + "：" + error.getDefaultMessage())
                .collect(Collectors.joining("；"));
        return defaultMessage(message);
    }

    private String defaultMessage(String message) {
        return message == null || message.isBlank() ? "请求参数不合法" : message;
    }
}
