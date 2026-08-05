package com.system.auth;

import com.system.auth.service.impl.AuthCaptchaServiceImpl;
import com.system.auth.vo.CaptchaVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuthCaptchaServiceImplTest {

    private AuthCaptchaServiceImpl captchaService;
    private ValueOperations<String, String> valueOperations;

    @SuppressWarnings("unchecked")
    @BeforeEach
    void setUp() {
        StringRedisTemplate redisTemplate = mock(StringRedisTemplate.class);
        valueOperations = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        captchaService = new AuthCaptchaServiceImpl(redisTemplate);
    }

    @Test
    void generateCaptchaShouldSaveCodeAndReturnImage() {
        CaptchaVO captcha = captchaService.generateCaptcha();

        assertNotNull(captcha.getCaptchaKey());
        assertTrue(captcha.getCaptchaImage().startsWith("data:image/png;base64,"));
        verify(valueOperations).set(startsWith("captcha:"), any(String.class), eq(Duration.ofMinutes(2)));
    }
}
