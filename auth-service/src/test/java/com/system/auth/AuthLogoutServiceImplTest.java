package com.system.auth;

import com.system.auth.config.JwtProperties;
import com.system.auth.service.TokenBlacklistService;
import com.system.auth.service.impl.AuthLogoutServiceImpl;
import com.system.auth.util.JwtUtil;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class AuthLogoutServiceImplTest {

    @Test
    void logoutShouldAddTokenToBlacklist() {
        JwtUtil jwtUtil = new JwtUtil(jwtProperties());
        String token = jwtUtil.generateToken(1L);
        AtomicReference<String> savedToken = new AtomicReference<>();
        AtomicReference<Duration> savedTtl = new AtomicReference<>();
        TokenBlacklistService blacklistService = (blackToken, ttl) -> {
            savedToken.set(blackToken);
            savedTtl.set(ttl);
        };

        new AuthLogoutServiceImpl(jwtUtil, blacklistService).logout(token);

        assertEquals(token, savedToken.get());
        assertFalse(savedTtl.get().isNegative());
    }

    private JwtProperties jwtProperties() {
        JwtProperties jwtProperties = new JwtProperties();
        jwtProperties.setSecret("abc123456abc123456abc123456abc123456");
        jwtProperties.setExpire(7200L);
        return jwtProperties;
    }
}
