package com.system.service.impl;

import com.system.common.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * 验证码服务测试。
 */
class CaptchaServiceImplTest {

    private CaptchaServiceImpl captchaService;
    private ValueOperations<String, String> valueOperations;

    @SuppressWarnings("unchecked")
    @BeforeEach
    void setUp() {
        captchaService = new CaptchaServiceImpl();
        StringRedisTemplate stringRedisTemplate = mock(StringRedisTemplate.class);
        valueOperations = mock(ValueOperations.class);
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        ReflectionTestUtils.setField(captchaService, "stringRedisTemplate", stringRedisTemplate);
    }

    @Test
    void validateCaptchaShouldPassWhenCodeMatches() {
        when(valueOperations.getAndDelete("captcha:test-key")).thenReturn("A1B2");

        assertDoesNotThrow(() -> captchaService.validateCaptcha("test-key", "a1b2"));
    }

    @Test
    void validateCaptchaShouldFailWhenCodeMissing() {
        when(valueOperations.getAndDelete("captcha:test-key")).thenReturn(null);

        assertThrows(BusinessException.class, () -> captchaService.validateCaptcha("test-key", "A1B2"));
    }

    @Test
    void validateCaptchaShouldFailWhenCodeNotMatches() {
        when(valueOperations.getAndDelete("captcha:test-key")).thenReturn("A1B2");

        assertThrows(BusinessException.class, () -> captchaService.validateCaptcha("test-key", "Z9Y8"));
    }
}
