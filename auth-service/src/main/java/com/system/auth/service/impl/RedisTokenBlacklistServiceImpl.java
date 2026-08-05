package com.system.auth.service.impl;

import com.system.auth.service.TokenBlacklistService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.HexFormat;

/**
 * Redis Token黑名单实现。
 */
@Service
public class RedisTokenBlacklistServiceImpl implements TokenBlacklistService {

    private static final String TOKEN_BLACKLIST_KEY_PREFIX = "token:blacklist:";

    private final StringRedisTemplate stringRedisTemplate;

    public RedisTokenBlacklistServiceImpl(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public void addToBlacklist(String token, Duration ttl) {
        if (token == null || token.isBlank() || ttl == null || ttl.isZero() || ttl.isNegative()) {
            return;
        }
        stringRedisTemplate.opsForValue().set(TOKEN_BLACKLIST_KEY_PREFIX + sha256(token), "1", ttl);
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
