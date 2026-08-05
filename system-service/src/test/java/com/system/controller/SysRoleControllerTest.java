package com.system.controller;

import com.system.common.GlobalExceptionHandler;
import com.system.common.PageResult;
import com.system.dto.RoleAssignPermissionDTO;
import com.system.dto.RoleAddDTO;
import com.system.dto.RoleSearchDTO;
import com.system.dto.RoleUpdateDTO;
import com.system.service.SysRoleService;
import com.system.vo.MenuTreeVO;
import com.system.vo.RolePageVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SysRoleControllerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        SysRoleController sysRoleController = new SysRoleController();
        ReflectionTestUtils.setField(sysRoleController, "sysRoleService", new TestSysRoleService());
        mockMvc = MockMvcBuilders.standaloneSetup(sysRoleController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator())
                .build();
    }

    @Test
    void getRoleListShouldReturnPage() throws Exception {
        mockMvc.perform(get("/system/role/search_list?pageNum=1&pageSize=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.list[0].roleCode").value("super_admin"))
                .andExpect(jsonPath("$.data.list[0].status").value(1));
    }

    @Test
    void addRoleShouldReturnSuccess() throws Exception {
        mockMvc.perform(post("/system/role/add")
                        .contentType("application/json")
                        .content("""
                                {
                                  "roleName": "测试角色",
                                  "roleCode": "test_role",
                                  "sort": 3
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value("新增角色成功"));
    }

    @Test
    void addRoleShouldValidateRoleCode() throws Exception {
        mockMvc.perform(post("/system/role/add")
                        .contentType("application/json")
                        .content("""
                                {
                                  "roleName": "测试角色",
                                  "roleCode": "",
                                  "sort": 3
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.isSuccess").value(false));
    }

    @Test
    void addRoleShouldValidateMissingRoleCode() throws Exception {
        mockMvc.perform(post("/system/role/add")
                        .contentType("application/json")
                        .content("""
                                {
                                  "roleName": "测试角色"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.isSuccess").value(false));
    }

    @Test
    void editRoleShouldReturnSuccess() throws Exception {
        mockMvc.perform(post("/system/role/modify")
                        .contentType("application/json")
                        .content("""
                                {
                                  "id": 1,
                                  "roleName": "超级管理员",
                                  "roleCode": "super_admin",
                                  "sort": 1
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    void editRoleShouldAllowPartialUpdate() throws Exception {
        mockMvc.perform(post("/system/role/modify")
                        .contentType("application/json")
                        .content("""
                                {
                                  "id": 2,
                                  "roleName": "普通后台用户"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    void updateRoleStatusShouldReturnSuccess() throws Exception {
        mockMvc.perform(put("/system/role/status")
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
    void updateRoleStatusShouldValidateStatus() throws Exception {
        mockMvc.perform(put("/system/role/status")
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
    void getRolePermissionsShouldReturnTree() throws Exception {
        mockMvc.perform(get("/system/role/1/permissions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].name").value("系统管理"))
                .andExpect(jsonPath("$.data[0].children[0].name").value("用户管理"))
                .andExpect(jsonPath("$.data[0].children[0].children[0].permissionKey").value("sys:user:list"));
    }

    @Test
    void assignRolePermissionsShouldReturnSuccess() throws Exception {
        mockMvc.perform(put("/system/role/1/permissions")
                        .contentType("application/json")
                        .content("""
                                {
                                  "permissionIds": [1, 2, 3]
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    void assignRolePermissionsShouldValidatePermissionIds() throws Exception {
        mockMvc.perform(put("/system/role/1/permissions")
                        .contentType("application/json")
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.isSuccess").value(false));
    }

    @Test
    void deleteRoleShouldReturnSuccess() throws Exception {
        mockMvc.perform(delete("/system/role/delete/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value("删除角色成功"));
    }

    private Validator validator() {
        LocalValidatorFactoryBean validatorFactoryBean = new LocalValidatorFactoryBean();
        validatorFactoryBean.afterPropertiesSet();
        return validatorFactoryBean;
    }

    private static class TestSysRoleService implements SysRoleService {

        @Override
        public PageResult<RolePageVO> getRolePage(RoleSearchDTO dto, Integer pageNum, Integer pageSize) {
            RolePageVO vo = new RolePageVO();
            vo.setId(1L);
            vo.setRoleName("超级管理员");
            vo.setRoleCode("super_admin");
            vo.setSort(1);
            vo.setStatus(1);
            vo.setCreateTime(LocalDateTime.of(2026, 7, 30, 10, 0));
            return PageResult.build(1L, List.of(vo));
        }

        @Override
        public void saveRole(RoleAddDTO roleAddDTO) {
        }

        @Override
        public void editRole(RoleUpdateDTO roleUpdateDTO) {
        }

        @Override
        public void updateRoleStatus(com.system.dto.RoleStatusDTO roleStatusDTO) {
        }

        @Override
        public List<MenuTreeVO> getRolePermissions(Long id) {
            MenuTreeVO system = buildMenu(1L, 0L, "系统管理", "", 1);
            MenuTreeVO user = buildMenu(2L, 1L, "用户管理", "", 2);
            MenuTreeVO list = buildMenu(3L, 2L, "用户列表", "sys:user:list", 3);
            user.getChildren().add(list);
            system.getChildren().add(user);
            return List.of(system);
        }

        @Override
        public void assignRolePermissions(Long id, RoleAssignPermissionDTO roleAssignPermissionDTO) {
        }

        @Override
        public void deleteRole(Long id) {
        }

        private MenuTreeVO buildMenu(Long id, Long parentId, String name, String permissionKey, Integer type) {
            MenuTreeVO vo = new MenuTreeVO();
            vo.setId(id);
            vo.setParentId(parentId);
            vo.setName(name);
            vo.setPermissionKey(permissionKey);
            vo.setType(type);
            vo.setSort(1);
            vo.setVisible(1);
            vo.setStatus(1);
            vo.setCreateTime(LocalDateTime.of(2026, 7, 30, 14, 0));
            vo.setUpdateTime(LocalDateTime.of(2026, 7, 30, 14, 0));
            return vo;
        }
    }
}
