package com.system.auth.util;

import com.system.auth.config.JwtProperties;
import com.system.common.BusinessException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * 认证服务JWT工具。
 */
@Component
public class JwtUtil {

    private final JwtProperties jwtProperties;

    public JwtUtil(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    public String generateToken(Long userId) {
        long now = System.currentTimeMillis();
        long expTime = now + jwtProperties.getExpire() * 1000;
        return Jwts.builder()
                .subject(userId.toString())
                .issuedAt(new Date(now))
                .expiration(new Date(expTime))
                .signWith(getSecretKey(), Jwts.SIG.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            throw new BusinessException("登录令牌已过期，请重新登录");
        } catch (MalformedJwtException | SignatureException e) {
            throw new BusinessException("令牌无效，请重新登录");
        } catch (Exception e) {
            throw new BusinessException("登录校验失败，请重新登录");
        }
    }

    public long getRemainingMillis(String token) {
        return parseClaims(token).getExpiration().getTime() - System.currentTimeMillis();
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSecretKey() {
        byte[] bytes = jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(bytes);
    }
}
