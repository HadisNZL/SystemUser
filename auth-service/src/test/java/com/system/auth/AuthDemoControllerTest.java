package com.system.auth;

import com.system.auth.client.AdminSystemDemoClient;
import com.system.auth.controller.AuthDemoController;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthDemoControllerTest {

    @Test
    void pingShouldReturnUpstreamMessage() throws Exception {
        AdminSystemDemoClient client = () -> "pong from admin-system";
        AuthDemoController controller = new AuthDemoController(client);
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        mockMvc.perform(get("/auth/demo/ping"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.serviceName").value("auth-service"))
                .andExpect(jsonPath("$.upstreamMessage").value("pong from admin-system"));
    }
}
