package com.system.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.system.common.BusinessException;
import com.system.service.PermissionCacheService;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * 用户权限 Redis 缓存实现。
 */
@Service
public class PermissionCacheServiceImpl implements PermissionCacheService {

    private static final String USER_PERMISSION_KEY_PREFIX = "user:permissions:";
    private static final Duration PERMISSION_CACHE_TTL = Duration.ofMinutes(30);
    private static final TypeReference<List<String>> STRING_LIST_TYPE = new TypeReference<>() {
    };

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private ObjectMapper objectMapper;

    @Override
    public List<String> getUserPermissionKeys(Long userId, Supplier<List<String>> dbLoader) {
        String cacheKey = buildUserPermissionKey(userId);
        String cacheValue = stringRedisTemplate.opsForValue().get(cacheKey);
        if (cacheValue != null && !cacheValue.isBlank()) {
            return parsePermissionKeys(cacheValue);
        }
        List<String> permissionKeys = dbLoader.get();
        stringRedisTemplate.opsForValue().set(cacheKey, toJson(permissionKeys), PERMISSION_CACHE_TTL);
        return permissionKeys;
    }

    @Override
    public void clearUserPermissionCache(Long userId) {
        if (userId == null) {
            return;
        }
        stringRedisTemplate.delete(buildUserPermissionKey(userId));
    }

    @Override
    public void clearAllUserPermissionCache() {
        List<String> keys = new ArrayList<>();
        ScanOptions options = ScanOptions.scanOptions()
                .match(USER_PERMISSION_KEY_PREFIX + "*")
                .count(100)
                .build();
        try (Cursor<String> cursor = stringRedisTemplate.scan(options)) {
            cursor.forEachRemaining(keys::add);
        }
        if (!keys.isEmpty()) {
            stringRedisTemplate.delete(keys);
        }
    }

    private String buildUserPermissionKey(Long userId) {
        return USER_PERMISSION_KEY_PREFIX + userId;
    }

    private List<String> parsePermissionKeys(String cacheValue) {
        try {
            return objectMapper.readValue(cacheValue, STRING_LIST_TYPE);
        } catch (JsonProcessingException e) {
            throw new BusinessException("权限缓存解析失败");
        }
    }

    private String toJson(List<String> permissionKeys) {
        try {
            return objectMapper.writeValueAsString(permissionKeys);
        } catch (JsonProcessingException e) {
            throw new BusinessException("权限缓存写入失败");
        }
    }
}
