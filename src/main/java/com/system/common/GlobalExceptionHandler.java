package com.system.common;

import jakarta.validation.ConstraintViolationException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 捕获自定义业务异常
    @ExceptionHandler(BusinessException.class)
    public Result<?> businessExceptionHandler(BusinessException e) {
        return Result.fail(e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<?> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        return Result.fail("参数错误：" + buildFieldErrorMessage(e.getBindingResult().getFieldErrors()));
    }

    @ExceptionHandler(BindException.class)
    public Result<?> bindExceptionHandler(BindException e) {
        return Result.fail("参数错误：" + buildFieldErrorMessage(e.getBindingResult().getFieldErrors()));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public Result<?> constraintViolationExceptionHandler(ConstraintViolationException e) {
        String message = e.getConstraintViolations().stream()
                .map(item -> item.getPropertyPath() + "：" + item.getMessage())
                .collect(Collectors.joining("；"));
        return Result.fail("参数错误：" + defaultMessage(message));
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public Result<?> handlerMethodValidationExceptionHandler(HandlerMethodValidationException e) {
        return Result.fail("参数错误：" + defaultMessage(e.getMessage()));
    }

    // 捕获所有未知异常
    @ExceptionHandler(Exception.class)
    public Result<?> error(Exception e) {
        return Result.fail("server is error : " + e.getMessage());
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
