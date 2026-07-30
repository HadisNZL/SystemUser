package com.system.service;

import com.system.vo.CaptchaVO;

public interface CaptchaService {

    CaptchaVO generateCaptcha();

    void validateCaptcha(String captchaKey, String captchaCode);
}
