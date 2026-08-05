package com.system.aspect;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.system.annotation.OperationLog;
import com.system.common.log.OperationLogEvent;
import com.system.mq.OperationLogMessageProducer;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * 采集写操作并异步发送日志。
 */
@Aspect
@Component
public class OperationLogAspect {

    private static final int SUCCESS = 1;
    private static final int FAIL = 0;
    private static final int MAX_TEXT_LENGTH = 2000;

    private final OperationLogMessageProducer operationLogMessageProducer;
    private final ObjectMapper objectMapper;

    public OperationLogAspect(OperationLogMessageProducer operationLogMessageProducer, ObjectMapper objectMapper) {
        this.operationLogMessageProducer = operationLogMessageProducer;
        this.objectMapper = objectMapper;
    }

    @Around("@annotation(operationLog)")
    public Object around(ProceedingJoinPoint joinPoint, OperationLog operationLog) throws Throwable {
        long startTime = System.currentTimeMillis();
        OperationLogEvent event = buildBaseEvent(joinPoint, operationLog);
        try {
            Object result = joinPoint.proceed();
            event.setStatus(SUCCESS);
            event.setResponseResult(toJson(result));
            return result;
        } catch (Throwable e) {
            event.setStatus(FAIL);
            event.setErrorMsg(limit(e.getMessage()));
            throw e;
        } finally {
            event.setCostTime(System.currentTimeMillis() - startTime);
            operationLogMessageProducer.send(event);
        }
    }

    private OperationLogEvent buildBaseEvent(ProceedingJoinPoint joinPoint, OperationLog operationLog) {
        OperationLogEvent event = new OperationLogEvent();
        event.setModule(operationLog.module());
        event.setOperation(operationLog.operation());
        event.setRequestParams(toJson(joinPoint.getArgs()));
        event.setOperatorId(getOperatorId());
        event.setCreateTime(LocalDateTime.now());

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            event.setRequestMethod(request.getMethod());
            event.setRequestUri(request.getRequestURI());
            event.setIp(getClientIp(request));
        }
        return event;
    }

    private Long getOperatorId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            return null;
        }
        try {
            return Long.valueOf(authentication.getName());
        } catch (NumberFormatException e) {
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
        if (value instanceof byte[] bytes) {
            return "二进制数据，大小：" + bytes.length + "字节";
        }
        if (value instanceof ResponseEntity<?> response && response.getBody() instanceof byte[] bytes) {
            return "二进制响应，大小：" + bytes.length + "字节";
        }
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
