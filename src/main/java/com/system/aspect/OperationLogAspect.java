package com.system.aspect;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.system.annotation.OperationLog;
import com.system.entity.SysOperationLog;
import com.system.mapper.SysOperationLogMapper;
import com.system.util.SecurityUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;

@Slf4j
@Aspect
@Component
public class OperationLogAspect {

    private static final int SUCCESS = 1;
    private static final int FAIL = 0;
    private static final int MAX_TEXT_LENGTH = 2000;

    @Resource
    private SysOperationLogMapper sysOperationLogMapper;

    @Resource
    private ObjectMapper objectMapper;

    @Around("@annotation(operationLog)")
    public Object around(ProceedingJoinPoint joinPoint, OperationLog operationLog) throws Throwable {
        long startTime = System.currentTimeMillis();
        SysOperationLog log = buildBaseLog(joinPoint, operationLog);
        try {
            Object result = joinPoint.proceed();
            log.setStatus(SUCCESS);
            log.setResponseResult(toJson(result));
            return result;
        } catch (Throwable e) {
            log.setStatus(FAIL);
            log.setErrorMsg(limit(e.getMessage()));
            throw e;
        } finally {
            log.setCostTime(System.currentTimeMillis() - startTime);
            saveOperationLog(log);
        }
    }

    private void saveOperationLog(SysOperationLog logEntity) {
        try {
            sysOperationLogMapper.insert(logEntity);
        } catch (Exception e) {
            log.error("保存操作日志失败", e);
        }
    }

    private SysOperationLog buildBaseLog(ProceedingJoinPoint joinPoint, OperationLog operationLog) {
        SysOperationLog log = new SysOperationLog();
        log.setModule(operationLog.module());
        log.setOperation(operationLog.operation());
        log.setRequestParams(toJson(joinPoint.getArgs()));
        log.setOperatorId(getOperatorId());

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            log.setRequestMethod(request.getMethod());
            log.setRequestUri(request.getRequestURI());
            log.setIp(getClientIp(request));
        }
        return log;
    }

    private Long getOperatorId() {
        try {
            return SecurityUtil.getCurrentUserId();
        } catch (RuntimeException e) {
            return null;
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private String toJson(Object value) {
        try {
            return limit(maskSensitive(objectMapper.writeValueAsString(value)));
        } catch (JsonProcessingException e) {
            return limit(Arrays.toString(new Object[]{value}));
        }
    }

    private String maskSensitive(String text) {
        if (text == null) {
            return null;
        }
        return text.replaceAll("(?i)\"(password|oldPassword|newPassword|captchaCode)\"\\s*:\\s*\"[^\"]*\"", "\"$1\":\"******\"");
    }

    private String limit(String text) {
        if (text == null || text.length() <= MAX_TEXT_LENGTH) {
            return text;
        }
        return text.substring(0, MAX_TEXT_LENGTH);
    }
}
