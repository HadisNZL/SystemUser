package com.system.controller;

import com.system.common.GlobalExceptionHandler;
import com.system.dto.LoginDTO;
import com.system.service.LoginService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class LoginControllerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        LoginController loginController = new LoginController();
        LoginService loginService = new LoginService() {
            @Override
            public String login(LoginDTO loginDTO) {
                return "test-token";
            }
        };
        ReflectionTestUtils.setField(loginController, "loginService", loginService);

        mockMvc = MockMvcBuilders.standaloneSetup(loginController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator())
                .build();
    }

    @Test
    void loginShouldReturnTokenWhenRequestValid() throws Exception {
        mockMvc.perform(post("/sys/login")
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
                .andExpect(jsonPath("$.data").value("test-token"));
    }

    @Test
    void loginShouldReturnBadRequestWhenPasswordBlank() throws Exception {
        mockMvc.perform(post("/sys/login")
                        .contentType("application/json")
                        .content("""
                                {
                                  "username": "admin",
                                  "password": "",
                                  "captchaKey": "test-key",
                                  "captchaCode": "A1B2"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.msg").value(org.hamcrest.Matchers.containsString("密码不能为空")));
    }

    @Test
    void loginShouldReturnBadRequestWhenCaptchaBlank() throws Exception {
        mockMvc.perform(post("/sys/login")
                        .contentType("application/json")
                        .content("""
                                {
                                  "username": "admin",
                                  "password": "123456",
                                  "captchaKey": "test-key",
                                  "captchaCode": ""
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.msg").value(org.hamcrest.Matchers.containsString("验证码不能为空")));
    }

    private Validator validator() {
        LocalValidatorFactoryBean validatorFactoryBean = new LocalValidatorFactoryBean();
        validatorFactoryBean.afterPropertiesSet();
        return validatorFactoryBean;
    }
}
