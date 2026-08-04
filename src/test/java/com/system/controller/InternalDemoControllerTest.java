package com.system.controller;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class InternalDemoControllerTest {

    @Test
    void pingShouldReturnPlainText() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new InternalDemoController()).build();

        mockMvc.perform(get("/internal/demo/ping"))
                .andExpect(status().isOk())
                .andExpect(content().string("pong from admin-system"));
    }
}
