package com.system.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Token黑名单服务测试。
 */
class TokenBlacklistServiceImplTest {

    private static final String TEST_TOKEN_BLACKLIST_KEY = "token:blacklist:4c5dc9b7708905f77f5e5d16316b5dfb425e68cb326dcd55a860e90a7707031e";

    private TokenBlacklistServiceImpl tokenBlacklistService;
    private StringRedisTemplate stringRedisTemplate;
    private ValueOperations<String, String> valueOperations;

    @SuppressWarnings("unchecked")
    @BeforeEach
    void setUp() {
        tokenBlacklistService = new TokenBlacklistServiceImpl();
        stringRedisTemplate = mock(StringRedisTemplate.class);
        valueOperations = mock(ValueOperations.class);
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        ReflectionTestUtils.setField(tokenBlacklistService, "stringRedisTemplate", stringRedisTemplate);
    }

    @Test
    void addToBlacklistShouldWriteTokenDigestKey() {
        tokenBlacklistService.addToBlacklist("test-token", Duration.ofMinutes(10));

        verify(valueOperations).set(eq(TEST_TOKEN_BLACKLIST_KEY), eq("1"), eq(Duration.ofMinutes(10)));
    }

    @Test
    void isBlacklistedShouldReturnTrueWhenKeyExists() {
        when(stringRedisTemplate.hasKey(TEST_TOKEN_BLACKLIST_KEY)).thenReturn(true);

        assertTrue(tokenBlacklistService.isBlacklisted("test-token"));
    }

    @Test
    void isBlacklistedShouldReturnFalseWhenTokenBlank() {
        assertFalse(tokenBlacklistService.isBlacklisted(""));
    }
}
