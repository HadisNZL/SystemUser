package com.system.controller;

import com.system.common.GlobalExceptionHandler;
import com.system.common.PageResult;
import com.system.dto.RoleAddDTO;
import com.system.dto.RoleSearchDTO;
import com.system.dto.RoleUpdateDTO;
import com.system.service.SysRoleService;
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
        mockMvc.perform(get("/sys/role/search_list?pageNum=1&pageSize=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.list[0].roleCode").value("super_admin"))
                .andExpect(jsonPath("$.data.list[0].status").value(1));
    }

    @Test
    void addRoleShouldReturnSuccess() throws Exception {
        mockMvc.perform(post("/sys/role/add")
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
        mockMvc.perform(post("/sys/role/add")
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
        mockMvc.perform(post("/sys/role/add")
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
        mockMvc.perform(post("/sys/role/modify")
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
        mockMvc.perform(post("/sys/role/modify")
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
        mockMvc.perform(put("/sys/role/status")
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
        mockMvc.perform(put("/sys/role/status")
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
    void deleteRoleShouldReturnSuccess() throws Exception {
        mockMvc.perform(delete("/sys/role/delete/1"))
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
        public void deleteRole(Long id) {
        }
    }
}
