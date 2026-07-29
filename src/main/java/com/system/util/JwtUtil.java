package com.system.util;

import com.system.common.BusinessException;
import com.system.config.prop.JwtProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    @Resource
    private JwtProperties jwtProperties;

    /**
     * 获取加密密钥
     */
    private SecretKey getSecretKey() {
        byte[] bytes = jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(bytes);
    }


    /**
     * 生成Token，存储userId
     */
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

    /**
     * 根据Token获取用户ID
     */
    public Long getUserId(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return Long.parseLong(claims.getSubject());
    }

    /**
     * 校验token有效性、是否过期
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSecretKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            throw new BusinessException("登录令牌已过期，请重新登录");
        } catch (MalformedJwtException | SignatureException | IncorrectClaimException e) {
            throw new BusinessException("令牌无效，请重新登录");
        } catch (Exception e) {
            throw new BusinessException("登录校验失败，请重新登录");
        }
    }

}
