package com.system.controller;

import com.system.common.GlobalExceptionHandler;
import com.system.dto.UserAssignRoleDTO;
import com.system.service.SysUserService;
import com.system.vo.RolePageVO;
import com.system.vo.UserDetailVO;
import com.system.vo.UserImportResultVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
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

    @Test
    void getUserRolesShouldReturnRoles() throws Exception {
        mockMvc.perform(get("/sys/user/1/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].roleName").value("超级管理员"))
                .andExpect(jsonPath("$.data[0].roleCode").value("super_admin"));
    }

    @Test
    void assignUserRolesShouldReturnSuccess() throws Exception {
        mockMvc.perform(put("/sys/user/1/roles")
                        .contentType("application/json")
                        .content("""
                                {
                                  "roleIds": [1, 2]
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    void assignUserRolesShouldValidateRoleIds() throws Exception {
        mockMvc.perform(put("/sys/user/1/roles")
                        .contentType("application/json")
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.isSuccess").value(false));
    }

    @Test
    void exportUserExcelShouldReturnAttachment() throws Exception {
        mockMvc.perform(get("/sys/user/export"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", containsString(".xlsx")))
                .andExpect(content().bytes("excel".getBytes()));
    }

    @Test
    void getUserImportTemplateShouldReturnAttachment() throws Exception {
        mockMvc.perform(get("/sys/user/import-template"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", containsString(".xlsx")))
                .andExpect(content().bytes("template".getBytes()));
    }

    @Test
    void importUserExcelShouldReturnResult() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "users.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "excel".getBytes());

        mockMvc.perform(multipart("/sys/user/import").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.successCount").value(1))
                .andExpect(jsonPath("$.data.failureCount").value(0));
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
        public byte[] exportUserExcel(com.system.dto.UserSearchDTO dto) {
            return "excel".getBytes();
        }

        @Override
        public byte[] getUserImportTemplate() {
            return "template".getBytes();
        }

        @Override
        public UserImportResultVO importUserExcel(MultipartFile file) {
            UserImportResultVO vo = new UserImportResultVO();
            vo.setSuccessCount(1);
            vo.setFailureCount(0);
            return vo;
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
        public List<RolePageVO> getUserRoles(Long id) {
            RolePageVO vo = new RolePageVO();
            vo.setId(1L);
            vo.setRoleName("超级管理员");
            vo.setRoleCode("super_admin");
            vo.setSort(1);
            vo.setStatus(1);
            vo.setCreateTime(LocalDateTime.of(2026, 7, 29, 10, 0));
            return List.of(vo);
        }

        @Override
        public void assignUserRoles(Long id, UserAssignRoleDTO userAssignRoleDTO) {
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
