package com.system.controller;

import com.system.common.PageResult;
import com.system.dto.OperationLogSearchDTO;
import com.system.service.OperationLogQueryService;
import com.system.vo.OperationLogVO;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 操作日志查询接口测试。
 */
class OperationLogControllerTest {

    @Test
    void searchListShouldReturnPage() throws Exception {
        OperationLogQueryService service = (dto, pageNum, pageSize) -> {
            OperationLogVO vo = new OperationLogVO();
            vo.setId(2083000175466446800L);
            vo.setModule(dto.getModule());
            vo.setOperation("新增用户");
            vo.setStatus(1);
            vo.setOperatorId(1L);
            vo.setCreateTime(LocalDateTime.of(2026, 8, 4, 17, 0));
            return PageResult.build(1L, List.of(vo));
        };
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new OperationLogController(service)).build();

        mockMvc.perform(get("/log/operation/search_list")
                        .param("module", "用户管理")
                        .param("pageNum", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.list[0].id").value("2083000175466446800"))
                .andExpect(jsonPath("$.data.list[0].module").value("用户管理"));
    }
}
