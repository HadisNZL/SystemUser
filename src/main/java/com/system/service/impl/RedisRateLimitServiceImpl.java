package com.system.service.impl;

import com.system.common.BusinessException;
import com.system.common.ResultCode;
import com.system.service.RateLimitService;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Redis接口限流实现。
 */
@Service
public class RedisRateLimitServiceImpl implements RateLimitService {

    private static final String RATE_LIMIT_KEY_PREFIX = "rate_limit:";
    private static final DefaultRedisScript<Long> RATE_LIMIT_SCRIPT = new DefaultRedisScript<>("""
            local current = redis.call('incr', KEYS[1])
            if current == 1 then
                redis.call('expire', KEYS[1], ARGV[1])
            end
            return current
            """, Long.class);

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void checkLimit(String key, int seconds, int maxCount) {
        Long count = stringRedisTemplate.execute(RATE_LIMIT_SCRIPT, List.of(buildRateLimitKey(key)), String.valueOf(seconds));
        if (count > maxCount) {
            throw new BusinessException(ResultCode.RATE_LIMIT);
        }
    }

    private String buildRateLimitKey(String key) {
        return RATE_LIMIT_KEY_PREFIX + key;
    }
}
