package com.system.controller;

import com.system.common.PageResult;
import com.system.dto.UserAssignRoleDTO;
import com.system.dto.UserSearchDTO;
import com.system.service.SysUserService;
import com.system.vo.RolePageVO;
import com.system.vo.UserDetailVO;
import com.system.vo.UserImportResultVO;
import com.system.vo.UserPageVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.util.ReflectionTestUtils;
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

class SystemUserControllerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        SystemUserController controller = new SystemUserController();
        ReflectionTestUtils.setField(controller, "sysUserService", new FakeSysUserService());
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void getUserDetailShouldReturnUserDetail() throws Exception {
        mockMvc.perform(get("/system/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.username").value("admin"))
                .andExpect(jsonPath("$.data.password").doesNotExist())
                .andExpect(jsonPath("$.data.deleteFlag").doesNotExist())
                .andExpect(jsonPath("$.data.version").doesNotExist());
    }

    @Test
    void searchListReturnsPage() throws Exception {
        mockMvc.perform(get("/system/user/search_list")
                        .param("username", "admin")
                        .param("pageNum", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.list[0].username").value("admin"));
    }

    @Test
    void updateUserStatusShouldReturnSuccess() throws Exception {
        mockMvc.perform(put("/system/user/status")
                        .contentType("application/json")
                        .content("""
                                {
                                  "id": 1,
                                  "status": 0
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    void getUserRolesShouldReturnRoles() throws Exception {
        mockMvc.perform(get("/system/user/1/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].roleName").value("超级管理员"));
    }

    @Test
    void exportUserExcelShouldReturnAttachment() throws Exception {
        mockMvc.perform(get("/system/user/export"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", containsString(".xlsx")))
                .andExpect(content().bytes("excel".getBytes()));
    }

    @Test
    void importUserExcelShouldReturnResult() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "users.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "excel".getBytes());
        mockMvc.perform(multipart("/system/user/import").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.successCount").value(1));
    }

    static class FakeSysUserService implements SysUserService {
        @Override
        public UserDetailVO getUserDetail(Long id) {
            UserDetailVO vo = new UserDetailVO();
            vo.setId(id);
            vo.setUsername("admin");
            vo.setNickname("管理员");
            vo.setPhone("13800138000");
            vo.setEmail("admin@test.com");
            vo.setStatus(1);
            vo.setCreateTime(LocalDateTime.of(2026, 8, 4, 13, 0));
            vo.setUpdateTime(LocalDateTime.of(2026, 8, 4, 14, 0));
            return vo;
        }

        @Override
        public PageResult<UserPageVO> getUserPage(UserSearchDTO dto, Integer pageNum, Integer pageSize) {
            UserPageVO vo = new UserPageVO();
            vo.setId(1L);
            vo.setUsername(dto.getUsername());
            vo.setNickname("管理员");
            vo.setPhone("13800138000");
            vo.setEmail("admin@test.com");
            vo.setStatus(1);
            vo.setCreateTime(LocalDateTime.of(2026, 8, 4, 13, 0));
            return PageResult.build(1L, List.of(vo));
        }

        @Override
        public byte[] exportUserExcel(UserSearchDTO dto) {
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
            return vo;
        }

        @Override
        public void saveUser(com.system.dto.UserAddDTO user) {
        }

        @Override
        public void editUser(com.system.dto.UserUpdateDTO user) {
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
            return List.of(vo);
        }

        @Override
        public void assignUserRoles(Long id, UserAssignRoleDTO userAssignRoleDTO) {
        }

        @Override
        public void deleteUser(Long id) {
        }

        @Override
        public void adminPhysicalDeleteUser(Long id) {
        }
    }
}
