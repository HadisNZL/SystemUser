package com.system.controller;

import com.system.config.prop.SystemInfoProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SysConfigControllerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        SystemInfoProperties properties = new SystemInfoProperties();
        properties.setName("admin-system");
        properties.setDescription("企业级后台管理系统");
        properties.setVersion("0.0.1");

        SysConfigController controller = new SysConfigController();
        ReflectionTestUtils.setField(controller, "systemInfoProperties", properties);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void getSystemInfoShouldReturnConfiguredValue() throws Exception {
        mockMvc.perform(get("/sys/config/info"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.name").value("admin-system"))
                .andExpect(jsonPath("$.data.version").value("0.0.1"));
    }
}
