package com.system.controller;

import com.system.common.GlobalExceptionHandler;
import com.system.service.SysUserService;
import com.system.vo.UserDetailVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SysUserControllerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        SysUserController sysUserController = new SysUserController();
        SysUserService sysUserService = new TestSysUserService();
        ReflectionTestUtils.setField(sysUserController, "sysUserService", sysUserService);

        mockMvc = MockMvcBuilders.standaloneSetup(sysUserController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator())
                .build();
    }

    @Test
    void getUserDetailShouldReturnUserDetail() throws Exception {
        mockMvc.perform(get("/sys/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.username").value("admin"))
                .andExpect(jsonPath("$.data.password").doesNotExist())
                .andExpect(jsonPath("$.data.deleteFlag").doesNotExist())
                .andExpect(jsonPath("$.data.version").doesNotExist());
    }

    @Test
    void updateUserStatusShouldReturnSuccess() throws Exception {
        mockMvc.perform(put("/sys/user/status")
                        .contentType("application/json")
                        .content("""
                                {
                                  "id": 1,
                                  "status": 0
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    void updateUserStatusShouldValidateStatus() throws Exception {
        mockMvc.perform(put("/sys/user/status")
                        .contentType("application/json")
                        .content("""
                                {
                                  "id": 1,
                                  "status": 3
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.isSuccess").value(false));
    }

    @Test
    void resetPasswordShouldReturnSuccess() throws Exception {
        mockMvc.perform(put("/sys/user/reset-password")
                        .contentType("application/json")
                        .content("""
                                {
                                  "id": 1,
                                  "newPassword": "654321"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    void resetPasswordShouldValidatePasswordLength() throws Exception {
        mockMvc.perform(put("/sys/user/reset-password")
                        .contentType("application/json")
                        .content("""
                                {
                                  "id": 1,
                                  "newPassword": "123"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.isSuccess").value(false));
    }

    @Test
    void changePasswordShouldReturnSuccess() throws Exception {
        mockMvc.perform(put("/sys/user/change-password")
                        .contentType("application/json")
                        .content("""
                                {
                                  "oldPassword": "123456",
                                  "newPassword": "654321"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    void changePasswordShouldValidateOldPassword() throws Exception {
        mockMvc.perform(put("/sys/user/change-password")
                        .contentType("application/json")
                        .content("""
                                {
                                  "oldPassword": "",
                                  "newPassword": "654321"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.isSuccess").value(false));
    }

    private Validator validator() {
        LocalValidatorFactoryBean validatorFactoryBean = new LocalValidatorFactoryBean();
        validatorFactoryBean.afterPropertiesSet();
        return validatorFactoryBean;
    }

    private static class TestSysUserService implements SysUserService {

        @Override
        public UserDetailVO getUserDetail(Long id) {
            UserDetailVO vo = new UserDetailVO();
            vo.setId(id);
            vo.setUsername("admin");
            vo.setNickname("管理员");
            vo.setPhone("13800138000");
            vo.setEmail("admin@example.com");
            vo.setStatus(1);
            vo.setCreateTime(LocalDateTime.of(2026, 7, 29, 10, 0));
            vo.setUpdateTime(LocalDateTime.of(2026, 7, 29, 11, 0));
            return vo;
        }

        @Override
        public com.system.common.PageResult<com.system.vo.UserPageVO> getUserPage(com.system.dto.UserSearchDTO dto, Integer pageNum, Integer pageSize) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void saveUser(com.system.dto.UserAddDTO user) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void editUser(com.system.dto.UserUpdateDTO user) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateUserStatus(com.system.dto.UserStatusDTO userStatusDTO) {
        }

        @Override
        public void resetPassword(com.system.dto.UserResetPasswordDTO resetPasswordDTO) {
        }

        @Override
        public void changePassword(com.system.dto.UserChangePasswordDTO changePasswordDTO) {
        }

        @Override
        public void deleteUser(Long id) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void adminPhysicalDeleteUser(Long id) {
            throw new UnsupportedOperationException();
        }
    }
}
