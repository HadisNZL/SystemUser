package com.system.controller;

import com.system.common.GlobalExceptionHandler;
import com.system.service.CaptchaService;
import com.system.vo.CaptchaVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CaptchaControllerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        CaptchaController captchaController = new CaptchaController();
        ReflectionTestUtils.setField(captchaController, "captchaService", new TestCaptchaService());
        mockMvc = MockMvcBuilders.standaloneSetup(captchaController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void getCaptchaShouldReturnCaptchaKeyAndImage() throws Exception {
        mockMvc.perform(get("/captcha"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.captchaKey").value("test-key"))
                .andExpect(jsonPath("$.data.captchaImage").value("data:image/png;base64,test-image"));
    }

    private static class TestCaptchaService implements CaptchaService {

        @Override
        public CaptchaVO generateCaptcha() {
            CaptchaVO captchaVO = new CaptchaVO();
            captchaVO.setCaptchaKey("test-key");
            captchaVO.setCaptchaImage("data:image/png;base64,test-image");
            return captchaVO;
        }

        @Override
        public void validateCaptcha(String captchaKey, String captchaCode) {
        }
    }
}
