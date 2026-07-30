package com.system.common;

import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import lombok.extern.slf4j.Slf4j;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 捕获自定义业务异常
    @ExceptionHandler(BusinessException.class)
    public Result<?> businessExceptionHandler(BusinessException e) {
        return Result.fail(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public Result<?> accessDeniedExceptionHandler(AccessDeniedException e) {
        return Result.fail(ResultCode.FORBIDDEN);
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

    @ExceptionHandler(HandlerMethodValidationException.class)
    public Result<?> handlerMethodValidationExceptionHandler(HandlerMethodValidationException e) {
        return Result.fail(ResultCode.BAD_REQUEST, "参数错误：" + defaultMessage(e.getMessage()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Result<?> httpMessageNotReadableExceptionHandler(HttpMessageNotReadableException e) {
        return Result.fail(ResultCode.BAD_REQUEST, "参数错误：请求体格式不正确");
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public Result<?> duplicateKeyExceptionHandler(DuplicateKeyException e) {
        return Result.fail(ResultCode.BUSINESS_ERROR, "数据已存在，请检查唯一字段");
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public Result<?> dataIntegrityViolationExceptionHandler(DataIntegrityViolationException e) {
        return Result.fail(ResultCode.BAD_REQUEST, "参数错误：请求数据不满足数据库约束");
    }

    // 捕获所有未知异常
    @ExceptionHandler(Exception.class)
    public Result<?> error(Exception e) {
        log.error("系统异常", e);
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
