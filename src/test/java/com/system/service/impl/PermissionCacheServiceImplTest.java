package com.system.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 权限缓存服务测试。
 */
class PermissionCacheServiceImplTest {

    private PermissionCacheServiceImpl permissionCacheService;
    private StringRedisTemplate stringRedisTemplate;
    private ValueOperations<String, String> valueOperations;

    @SuppressWarnings("unchecked")
    @BeforeEach
    void setUp() {
        permissionCacheService = new PermissionCacheServiceImpl();
        stringRedisTemplate = mock(StringRedisTemplate.class);
        valueOperations = mock(ValueOperations.class);
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        ReflectionTestUtils.setField(permissionCacheService, "stringRedisTemplate", stringRedisTemplate);
        ReflectionTestUtils.setField(permissionCacheService, "objectMapper", new ObjectMapper());
    }

    @Test
    void getUserPermissionKeysShouldReturnCacheWhenExists() {
        when(valueOperations.get("user:permissions:1")).thenReturn("[\"sys:user:list\"]");
        AtomicInteger loadCount = new AtomicInteger();

        List<String> permissions = permissionCacheService.getUserPermissionKeys(1L, () -> {
            loadCount.incrementAndGet();
            return List.of("sys:user:add");
        });

        assertEquals(List.of("sys:user:list"), permissions);
        assertEquals(0, loadCount.get());
    }

    @Test
    void getUserPermissionKeysShouldWriteCacheWhenMissing() {
        when(valueOperations.get("user:permissions:1")).thenReturn(null);

        List<String> permissions = permissionCacheService.getUserPermissionKeys(1L, () -> List.of("sys:user:list"));

        assertEquals(List.of("sys:user:list"), permissions);
        verify(valueOperations).set(eq("user:permissions:1"), eq("[\"sys:user:list\"]"), eq(Duration.ofMinutes(30)));
    }

    @Test
    void clearUserPermissionCacheShouldDeleteUserKey() {
        permissionCacheService.clearUserPermissionCache(1L);

        verify(stringRedisTemplate).delete("user:permissions:1");
    }
}
