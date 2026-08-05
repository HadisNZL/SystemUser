package com.system.auth.service;

import com.system.auth.vo.CaptchaVO;

/**
 * 认证验证码服务。
 */
public interface AuthCaptchaService {

    CaptchaVO generateCaptcha();
}
