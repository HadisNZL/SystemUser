package com.system.auth.service;

/**
 * 认证接口限流服务。
 */
public interface AuthRateLimitService {

    void checkLimit(String key, int seconds, int maxCount);
}
