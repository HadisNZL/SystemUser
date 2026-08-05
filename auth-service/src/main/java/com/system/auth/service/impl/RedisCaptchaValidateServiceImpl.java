package com.system.auth.service.impl;

import com.system.auth.service.CaptchaValidateService;
import com.system.common.BusinessException;
import com.system.common.ResultCode;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * Redis验证码校验实现。
 */
@Service
public class RedisCaptchaValidateServiceImpl implements CaptchaValidateService {

    private static final String CAPTCHA_KEY_PREFIX = "captcha:";

    private final StringRedisTemplate stringRedisTemplate;

    public RedisCaptchaValidateServiceImpl(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public void validateCaptcha(String captchaKey, String captchaCode) {
        String cacheCode = stringRedisTemplate.opsForValue().getAndDelete(CAPTCHA_KEY_PREFIX + captchaKey);
        if (cacheCode == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "验证码已过期，请重新获取");
        }
        if (captchaCode == null || !cacheCode.equalsIgnoreCase(captchaCode.trim())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "验证码错误");
        }
    }
}
