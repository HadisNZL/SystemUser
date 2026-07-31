package com.system.service;

/**
 * 接口限流服务。
 */
public interface RateLimitService {

    void checkLimit(String key, int seconds, int maxCount);
}
