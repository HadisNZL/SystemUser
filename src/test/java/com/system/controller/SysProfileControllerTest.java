package com.system.controller;

import com.system.common.GlobalExceptionHandler;
import com.system.service.SysProfileService;
import com.system.vo.ProfileVO;
import com.system.vo.RolePageVO;
import com.system.vo.UserDetailVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SysProfileControllerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        SysProfileController sysProfileController = new SysProfileController();
        ReflectionTestUtils.setField(sysProfileController, "sysProfileService", new TestSysProfileService());
        mockMvc = MockMvcBuilders.standaloneSetup(sysProfileController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void getCurrentProfileShouldReturnUserRolesAndPermissions() throws Exception {
        mockMvc.perform(get("/sys/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.user.username").value("admin"))
                .andExpect(jsonPath("$.data.roles[0].roleCode").value("super_admin"))
                .andExpect(jsonPath("$.data.permissions[0]").value("sys:user:list"));
    }

    private static class TestSysProfileService implements SysProfileService {

        @Override
        public ProfileVO getCurrentProfile() {
            UserDetailVO user = new UserDetailVO();
            user.setId(1L);
            user.setUsername("admin");
            user.setNickname("管理员");
            user.setStatus(1);
            user.setCreateTime(LocalDateTime.of(2026, 7, 30, 15, 0));
            user.setUpdateTime(LocalDateTime.of(2026, 7, 30, 15, 0));

            RolePageVO role = new RolePageVO();
            role.setId(1L);
            role.setRoleName("超级管理员");
            role.setRoleCode("super_admin");
            role.setStatus(1);
            role.setSort(1);
            role.setCreateTime(LocalDateTime.of(2026, 7, 30, 15, 0));

            ProfileVO profile = new ProfileVO();
            profile.setUser(user);
            profile.setRoles(List.of(role));
            profile.setPermissions(List.of("sys:user:list", "sys:user:add"));
            return profile;
        }
    }
}
