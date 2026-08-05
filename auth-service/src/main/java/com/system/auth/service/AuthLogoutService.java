package com.system.auth.service;

/**
 * 认证退出服务。
 */
public interface AuthLogoutService {

    void logout(String token);
}
