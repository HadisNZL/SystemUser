package com.system.controller;

import com.system.common.GlobalExceptionHandler;
import com.system.service.InternalAuthService;
import com.system.vo.InternalLoginUserVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 内部认证接口测试。
 */
class InternalAuthControllerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        InternalAuthService internalAuthService = username -> {
            InternalLoginUserVO vo = new InternalLoginUserVO();
            vo.setId(1L);
            vo.setUsername(username);
            vo.setPassword("$2a$10$test");
            vo.setStatus(1);
            return vo;
        };
        mockMvc = MockMvcBuilders.standaloneSetup(new InternalAuthController(internalAuthService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void getLoginUserShouldReturnUserWhenHeaderValid() throws Exception {
        mockMvc.perform(get("/internal/auth/login-user")
                        .param("username", "admin")
                        .header("X-Internal-Source", "auth-service"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.username").value("admin"));
    }

    @Test
    void getLoginUserShouldReturnForbiddenWhenHeaderInvalid() throws Exception {
        mockMvc.perform(get("/internal/auth/login-user")
                        .param("username", "admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(403))
                .andExpect(jsonPath("$.isSuccess").value(false));
    }
}
