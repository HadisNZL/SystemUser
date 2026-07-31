package com.system.service.impl;

import com.system.service.TokenBlacklistService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.HexFormat;

/**
 * Token黑名单 Redis 实现。
 */
@Service
public class TokenBlacklistServiceImpl implements TokenBlacklistService {

    private static final String TOKEN_BLACKLIST_KEY_PREFIX = "token:blacklist:";

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void addToBlacklist(String token, Duration ttl) {
        if (token == null || token.isBlank() || ttl == null || ttl.isZero() || ttl.isNegative()) {
            return;
        }
        stringRedisTemplate.opsForValue().set(buildBlacklistKey(token), "1", ttl);
    }

    @Override
    public boolean isBlacklisted(String token) {
        if (token == null || token.isBlank()) {
            return false;
        }
        return stringRedisTemplate.hasKey(buildBlacklistKey(token));
    }

    private String buildBlacklistKey(String token) {
        return TOKEN_BLACKLIST_KEY_PREFIX + sha256(token);
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
