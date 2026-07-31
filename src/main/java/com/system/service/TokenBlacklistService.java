package com.system.service;

import java.time.Duration;

/**
 * Token黑名单服务。
 */
public interface TokenBlacklistService {

    void addToBlacklist(String token, Duration ttl);

    boolean isBlacklisted(String token);
}
