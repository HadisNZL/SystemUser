package com.system.aspect;

import com.system.annotation.RateLimit;
import com.system.service.RateLimitService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 接口限流切面。
 */
@Aspect
@Order(0)
@Component
public class RateLimitAspect {

    @Resource
    private RateLimitService rateLimitService;

    @Before("@annotation(rateLimit)")
    public void before(JoinPoint joinPoint, RateLimit rateLimit) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return;
        }
        HttpServletRequest request = attributes.getRequest();
        String businessKey = rateLimit.key().isBlank() ? request.getRequestURI() : rateLimit.key();
        String limitKey = businessKey + ":" + getClientIp(request);
        rateLimitService.checkLimit(limitKey, rateLimit.seconds(), rateLimit.maxCount());
    }

    private String getClientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
