package com.system.service.impl;

import com.system.common.BusinessException;
import com.system.common.ResultCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Redis限流服务测试。
 */
@SuppressWarnings("unchecked")
class RedisRateLimitServiceImplTest {

    private RedisRateLimitServiceImpl rateLimitService;
    private StringRedisTemplate stringRedisTemplate;

    @BeforeEach
    void setUp() {
        rateLimitService = new RedisRateLimitServiceImpl();
        stringRedisTemplate = mock(StringRedisTemplate.class);
        ReflectionTestUtils.setField(rateLimitService, "stringRedisTemplate", stringRedisTemplate);
    }

    @Test
    void checkLimitShouldPassWhenCountNotExceeded() {
        when(stringRedisTemplate.execute(any(RedisScript.class), eq(List.of("rate_limit:login:127.0.0.1")), eq("60"))).thenReturn(5L);

        rateLimitService.checkLimit("login:127.0.0.1", 60, 5);
    }

    @Test
    void checkLimitShouldThrowWhenCountExceeded() {
        when(stringRedisTemplate.execute(any(RedisScript.class), eq(List.of("rate_limit:login:127.0.0.1")), eq("60"))).thenReturn(6L);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> rateLimitService.checkLimit("login:127.0.0.1", 60, 5));
        assertEquals(ResultCode.RATE_LIMIT.getCode(), exception.getCode());
    }
}
