package com.system.auth.service.impl;

import com.system.auth.service.AuthLogoutService;
import com.system.auth.service.TokenBlacklistService;
import com.system.auth.util.JwtUtil;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * 认证退出实现。
 */
@Service
public class AuthLogoutServiceImpl implements AuthLogoutService {

    private final JwtUtil jwtUtil;
    private final TokenBlacklistService tokenBlacklistService;

    public AuthLogoutServiceImpl(JwtUtil jwtUtil, TokenBlacklistService tokenBlacklistService) {
        this.jwtUtil = jwtUtil;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @Override
    public void logout(String token) {
        jwtUtil.validateToken(token);
        tokenBlacklistService.addToBlacklist(token, Duration.ofMillis(jwtUtil.getRemainingMillis(token)));
    }
}
