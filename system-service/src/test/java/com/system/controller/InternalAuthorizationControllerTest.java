package com.system.controller;

import com.system.common.GlobalExceptionHandler;
import com.system.service.InternalAuthorizationService;
import com.system.vo.InternalUserAuthorizationVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 内部授权接口测试。
 */
class InternalAuthorizationControllerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        InternalAuthorizationService service = userId -> {
            InternalUserAuthorizationVO vo = new InternalUserAuthorizationVO();
            vo.setUserId(userId);
            vo.setStatus(1);
            vo.setPermissionKeys(List.of("sys:log:list"));
            return vo;
        };
        mockMvc = MockMvcBuilders.standaloneSetup(new InternalAuthorizationController(service))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void getUserAuthorizationShouldReturnPermissions() throws Exception {
        mockMvc.perform(get("/internal/authorization/user")
                        .param("userId", "1")
                        .header("X-Internal-Source", "log-service"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.userId").value(1))
                .andExpect(jsonPath("$.data.permissionKeys[0]").value("sys:log:list"));
    }

    @Test
    void getUserAuthorizationShouldAllowFileService() throws Exception {
        mockMvc.perform(get("/internal/authorization/user")
                        .param("userId", "1")
                        .header("X-Internal-Source", "file-service"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void getUserAuthorizationShouldRejectUnknownSource() throws Exception {
        mockMvc.perform(get("/internal/authorization/user")
                        .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(403));
    }
}
