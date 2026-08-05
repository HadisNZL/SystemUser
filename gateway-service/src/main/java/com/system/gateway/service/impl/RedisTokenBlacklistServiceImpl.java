package com.system.gateway.service.impl;

import com.system.gateway.service.TokenBlacklistService;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

/**
 * 网关Redis Token黑名单实现。
 */
@Service
public class RedisTokenBlacklistServiceImpl implements TokenBlacklistService {

    private static final String TOKEN_BLACKLIST_KEY_PREFIX = "token:blacklist:";

    private final ReactiveStringRedisTemplate redisTemplate;

    public RedisTokenBlacklistServiceImpl(ReactiveStringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Mono<Boolean> isBlacklisted(String token) {
        if (token == null || token.isBlank()) {
            return Mono.just(false);
        }
        return redisTemplate.hasKey(TOKEN_BLACKLIST_KEY_PREFIX + sha256(token));
    }

    private String sha256(String text) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(text.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(bytes);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256算法不可用", e);
        }
    }
}
