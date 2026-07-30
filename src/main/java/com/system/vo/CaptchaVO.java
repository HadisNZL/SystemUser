package com.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "验证码返回对象")
public class CaptchaVO {

    @Schema(description = "验证码唯一标识")
    private String captchaKey;

    @Schema(description = "Base64图片，前端可直接放到img src")
    private String captchaImage;
}
