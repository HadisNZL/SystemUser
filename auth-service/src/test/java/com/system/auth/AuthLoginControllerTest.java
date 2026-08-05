package com.system.auth;

import com.system.auth.common.GlobalExceptionHandler;
import com.system.auth.controller.AuthLoginController;
import com.system.auth.service.AuthCaptchaService;
import com.system.auth.service.AuthLoginService;
import com.system.auth.service.AuthLogoutService;
import com.system.auth.service.AuthRateLimitService;
import com.system.auth.vo.CaptchaVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthLoginControllerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        AuthLoginService authLoginService = loginDTO -> "auth-token";
        AuthCaptchaService authCaptchaService = () -> {
            CaptchaVO captchaVO = new CaptchaVO();
            captchaVO.setCaptchaKey("test-key");
            captchaVO.setCaptchaImage("data:image/png;base64,test-image");
            return captchaVO;
        };
        AuthLogoutService authLogoutService = token -> {
        };
        AuthRateLimitService authRateLimitService = (key, seconds, maxCount) -> {
        };
        mockMvc = MockMvcBuilders.standaloneSetup(new AuthLoginController(authLoginService, authCaptchaService, authLogoutService, authRateLimitService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator())
                .build();
    }

    @Test
    void captchaShouldReturnCaptchaKeyAndImage() throws Exception {
        mockMvc.perform(get("/auth/captcha"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.captchaKey").value("test-key"))
                .andExpect(jsonPath("$.data.captchaImage").value("data:image/png;base64,test-image"));
    }

    @Test
    void loginShouldReturnTokenWhenRequestValid() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType("application/json")
                        .content("""
                                {
                                  "username": "admin",
                                  "password": "123456",
                                  "captchaKey": "test-key",
                                  "captchaCode": "A1B2"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.data").value("auth-token"));
    }

    @Test
    void loginShouldReturnBadRequestWhenUsernameBlank() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType("application/json")
                        .content("""
                                {
                                  "username": "",
                                  "password": "123456",
                                  "captchaKey": "test-key",
                                  "captchaCode": "A1B2"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.msg").value(containsString("账号不能为空")));
    }

    @Test
    void logoutShouldReturnSuccessWhenTokenExists() throws Exception {
        mockMvc.perform(post("/auth/logout")
                        .header("Authorization", "Bearer test-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(true));
    }

    private Validator validator() {
        LocalValidatorFactoryBean validatorFactoryBean = new LocalValidatorFactoryBean();
        validatorFactoryBean.afterPropertiesSet();
        return validatorFactoryBean;
    }
}
