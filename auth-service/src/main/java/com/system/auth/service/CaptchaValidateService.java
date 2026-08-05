package com.system.auth.service;

/**
 * 验证码校验业务。
 */
public interface CaptchaValidateService {

    void validateCaptcha(String captchaKey, String captchaCode);
}
