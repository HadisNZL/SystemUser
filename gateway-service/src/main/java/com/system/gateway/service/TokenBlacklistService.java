package com.system.gateway.service;

import reactor.core.publisher.Mono;

/**
 * 网关Token黑名单服务。
 */
public interface TokenBlacklistService {

    Mono<Boolean> isBlacklisted(String token);
}
