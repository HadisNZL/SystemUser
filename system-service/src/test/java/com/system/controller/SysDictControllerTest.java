package com.system.controller;

import com.system.common.GlobalExceptionHandler;
import com.system.common.PageResult;
import com.system.dto.DictDataAddDTO;
import com.system.dto.DictDataSearchDTO;
import com.system.dto.DictDataUpdateDTO;
import com.system.dto.DictTypeAddDTO;
import com.system.dto.DictTypeSearchDTO;
import com.system.dto.DictTypeUpdateDTO;
import com.system.service.SysDictService;
import com.system.vo.DictDataVO;
import com.system.vo.DictTypeVO;
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

class SysDictControllerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        SysDictController sysDictController = new SysDictController();
        ReflectionTestUtils.setField(sysDictController, "sysDictService", new TestSysDictService());
        mockMvc = MockMvcBuilders.standaloneSetup(sysDictController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator())
                .build();
    }

    @Test
    void getDictTypeListShouldReturnPage() throws Exception {
        mockMvc.perform(get("/system/dict/type/search_list?pageNum=1&pageSize=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.list[0].dictType").value("sys_user_status"));
    }

    @Test
    void addDictTypeShouldReturnSuccess() throws Exception {
        mockMvc.perform(post("/system/dict/type/add")
                        .contentType("application/json")
                        .content("""
                                {
                                  "dictName": "用户状态",
                                  "dictType": "sys_user_status"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value("新增字典类型成功"));
    }

    @Test
    void addDictTypeShouldValidateDictType() throws Exception {
        mockMvc.perform(post("/system/dict/type/add")
                        .contentType("application/json")
                        .content("""
                                {
                                  "dictName": "用户状态",
                                  "dictType": "SysUserStatus"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.isSuccess").value(false));
    }

    @Test
    void updateDictTypeShouldReturnSuccess() throws Exception {
        mockMvc.perform(post("/system/dict/type/modify")
                        .contentType("application/json")
                        .content("""
                                {
                                  "id": 1,
                                  "dictName": "用户状态"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    void deleteDictTypeShouldReturnSuccess() throws Exception {
        mockMvc.perform(delete("/system/dict/type/delete/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value("删除字典类型成功"));
    }

    @Test
    void getDictDataListShouldReturnPage() throws Exception {
        mockMvc.perform(get("/system/dict/data/search_list?dictType=sys_user_status&pageNum=1&pageSize=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.list[0].dictLabel").value("正常"));
    }

    @Test
    void getEnabledDictDataShouldReturnList() throws Exception {
        mockMvc.perform(get("/system/dict/data/type/sys_user_status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].dictValue").value("1"));
    }

    @Test
    void addDictDataShouldReturnSuccess() throws Exception {
        mockMvc.perform(post("/system/dict/data/add")
                        .contentType("application/json")
                        .content("""
                                {
                                  "dictType": "sys_user_status",
                                  "dictLabel": "正常",
                                  "dictValue": "1"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value("新增字典数据成功"));
    }

    @Test
    void addDictDataShouldValidateRequiredFields() throws Exception {
        mockMvc.perform(post("/system/dict/data/add")
                        .contentType("application/json")
                        .content("""
                                {
                                  "dictType": "sys_user_status"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.isSuccess").value(false));
    }

    @Test
    void updateDictDataShouldReturnSuccess() throws Exception {
        mockMvc.perform(post("/system/dict/data/modify")
                        .contentType("application/json")
                        .content("""
                                {
                                  "id": 1,
                                  "dictLabel": "正常"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    void deleteDictDataShouldReturnSuccess() throws Exception {
        mockMvc.perform(delete("/system/dict/data/delete/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value("删除字典数据成功"));
    }

    private Validator validator() {
        LocalValidatorFactoryBean validatorFactoryBean = new LocalValidatorFactoryBean();
        validatorFactoryBean.afterPropertiesSet();
        return validatorFactoryBean;
    }

    private static class TestSysDictService implements SysDictService {

        @Override
        public PageResult<DictTypeVO> getDictTypePage(DictTypeSearchDTO dto, Integer pageNum, Integer pageSize) {
            DictTypeVO vo = new DictTypeVO();
            vo.setId(1L);
            vo.setDictName("用户状态");
            vo.setDictType("sys_user_status");
            vo.setStatus(1);
            vo.setCreateTime(LocalDateTime.of(2026, 7, 31, 10, 0));
            return PageResult.build(1L, List.of(vo));
        }

        @Override
        public void addDictType(DictTypeAddDTO dto) {
        }

        @Override
        public void updateDictType(DictTypeUpdateDTO dto) {
        }

        @Override
        public void deleteDictType(Long id) {
        }

        @Override
        public PageResult<DictDataVO> getDictDataPage(DictDataSearchDTO dto, Integer pageNum, Integer pageSize) {
            return PageResult.build(1L, List.of(buildDictData()));
        }

        @Override
        public List<DictDataVO> getEnabledDictData(String dictType) {
            return List.of(buildDictData());
        }

        @Override
        public void addDictData(DictDataAddDTO dto) {
        }

        @Override
        public void updateDictData(DictDataUpdateDTO dto) {
        }

        @Override
        public void deleteDictData(Long id) {
        }

        private DictDataVO buildDictData() {
            DictDataVO vo = new DictDataVO();
            vo.setId(1L);
            vo.setDictType("sys_user_status");
            vo.setDictLabel("正常");
            vo.setDictValue("1");
            vo.setSort(1);
            vo.setStatus(1);
            vo.setCreateTime(LocalDateTime.of(2026, 7, 31, 10, 0));
            return vo;
        }
    }
}
