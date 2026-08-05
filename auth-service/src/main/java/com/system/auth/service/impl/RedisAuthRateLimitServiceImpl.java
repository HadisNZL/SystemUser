package com.system.auth.service.impl;

import com.system.auth.service.AuthRateLimitService;
import com.system.common.BusinessException;
import com.system.common.ResultCode;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Redis认证接口限流实现。
 */
@Service
public class RedisAuthRateLimitServiceImpl implements AuthRateLimitService {

    private static final String RATE_LIMIT_KEY_PREFIX = "rate_limit:";
    private static final DefaultRedisScript<Long> RATE_LIMIT_SCRIPT = new DefaultRedisScript<>("""
            local current = redis.call('incr', KEYS[1])
            if current == 1 then
                redis.call('expire', KEYS[1], ARGV[1])
            end
            return current
            """, Long.class);

    private final StringRedisTemplate stringRedisTemplate;

    public RedisAuthRateLimitServiceImpl(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public void checkLimit(String key, int seconds, int maxCount) {
        Long count = stringRedisTemplate.execute(RATE_LIMIT_SCRIPT, List.of(RATE_LIMIT_KEY_PREFIX + key), String.valueOf(seconds));
        if (count != null && count > maxCount) {
            throw new BusinessException(ResultCode.RATE_LIMIT);
        }
    }
}
