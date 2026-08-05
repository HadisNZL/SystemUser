package com.system.auth.vo;

/**
 * 验证码返回对象。
 */
public class CaptchaVO {

    private String captchaKey;

    private String captchaImage;

    public String getCaptchaKey() {
        return captchaKey;
    }

    public void setCaptchaKey(String captchaKey) {
        this.captchaKey = captchaKey;
    }

    public String getCaptchaImage() {
        return captchaImage;
    }

    public void setCaptchaImage(String captchaImage) {
        this.captchaImage = captchaImage;
    }
}
