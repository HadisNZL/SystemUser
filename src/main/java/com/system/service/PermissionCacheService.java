package com.system.service;

import java.util.List;
import java.util.function.Supplier;

/**
 * 用户权限缓存服务。
 */
public interface PermissionCacheService {

    List<String> getUserPermissionKeys(Long userId, Supplier<List<String>> dbLoader);

    void clearUserPermissionCache(Long userId);

    void clearAllUserPermissionCache();
}
