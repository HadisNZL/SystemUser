package com.system.controller;

import com.system.common.GlobalExceptionHandler;
import com.system.dto.MenuAddDTO;
import com.system.dto.MenuUpdateDTO;
import com.system.service.SysMenuService;
import com.system.vo.MenuTreeVO;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SysMenuControllerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        SysMenuController sysMenuController = new SysMenuController();
        ReflectionTestUtils.setField(sysMenuController, "sysMenuService", new TestSysMenuService());
        mockMvc = MockMvcBuilders.standaloneSetup(sysMenuController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator())
                .build();
    }

    @Test
    void getMenuTreeShouldReturnTree() throws Exception {
        mockMvc.perform(get("/sys/menu/tree"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].name").value("系统管理"))
                .andExpect(jsonPath("$.data[0].children[0].name").value("用户管理"))
                .andExpect(jsonPath("$.data[0].children[0].children[0].permissionKey").value("sys:user:list"));
    }

    @Test
    void getCurrentUserMenuTreeShouldReturnMenus() throws Exception {
        mockMvc.perform(get("/sys/menu/current"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].name").value("系统管理"))
                .andExpect(jsonPath("$.data[0].children[0].name").value("用户管理"));
    }

    @Test
    void addMenuShouldReturnSuccess() throws Exception {
        mockMvc.perform(post("/sys/menu/add")
                        .contentType("application/json")
                        .content("""
                                {
                                  "parentId": 2,
                                  "name": "导出用户",
                                  "permissionKey": "sys:user:export",
                                  "type": 3,
                                  "sort": 10
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value("新增菜单成功"));
    }

    @Test
    void addMenuShouldValidateName() throws Exception {
        mockMvc.perform(post("/sys/menu/add")
                        .contentType("application/json")
                        .content("""
                                {
                                  "name": "",
                                  "type": 3
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.isSuccess").value(false));
    }

    @Test
    void addMenuShouldValidateType() throws Exception {
        mockMvc.perform(post("/sys/menu/add")
                        .contentType("application/json")
                        .content("""
                                {
                                  "name": "测试菜单",
                                  "type": 5
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.isSuccess").value(false));
    }

    @Test
    void editMenuShouldReturnSuccess() throws Exception {
        mockMvc.perform(post("/sys/menu/modify")
                        .contentType("application/json")
                        .content("""
                                {
                                  "id": 3,
                                  "name": "用户列表",
                                  "sort": 1
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    void deleteMenuShouldReturnSuccess() throws Exception {
        mockMvc.perform(delete("/sys/menu/delete/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value("删除菜单成功"));
    }

    private Validator validator() {
        LocalValidatorFactoryBean validatorFactoryBean = new LocalValidatorFactoryBean();
        validatorFactoryBean.afterPropertiesSet();
        return validatorFactoryBean;
    }

    private static class TestSysMenuService implements SysMenuService {

        @Override
        public List<MenuTreeVO> getMenuTree() {
            MenuTreeVO system = buildMenu(1L, 0L, "系统管理", "", 1);
            MenuTreeVO user = buildMenu(2L, 1L, "用户管理", "", 2);
            MenuTreeVO list = buildMenu(3L, 2L, "用户列表", "sys:user:list", 3);
            user.getChildren().add(list);
            system.getChildren().add(user);
            return List.of(system);
        }

        @Override
        public List<MenuTreeVO> getCurrentUserMenuTree() {
            MenuTreeVO system = buildMenu(1L, 0L, "系统管理", "", 1);
            MenuTreeVO user = buildMenu(2L, 1L, "用户管理", "", 2);
            system.getChildren().add(user);
            return List.of(system);
        }

        @Override
        public void saveMenu(MenuAddDTO menuAddDTO) {
        }

        @Override
        public void editMenu(MenuUpdateDTO menuUpdateDTO) {
        }

        @Override
        public void deleteMenu(Long id) {
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
            vo.setCreateTime(LocalDateTime.of(2026, 7, 30, 11, 0));
            vo.setUpdateTime(LocalDateTime.of(2026, 7, 30, 11, 0));
            return vo;
        }
    }
}
