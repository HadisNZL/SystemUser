package com.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.system.common.BusinessException;
import com.system.common.PageResult;
import com.system.dto.OperationLogSearchDTO;
import com.system.entity.SysOperationLog;
import com.system.mapper.SysOperationLogMapper;
import com.system.vo.OperationLogVO;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * 操作日志分页查询服务测试。
 */
class OperationLogQueryServiceImplTest {

    @Test
    void getOperationLogPageShouldMapEntity() {
        SysOperationLogMapper mapper = mock(SysOperationLogMapper.class);
        SysOperationLog entity = new SysOperationLog();
        entity.setId(1L);
        entity.setModule("用户管理");
        entity.setOperation("新增用户");
        entity.setStatus(1);
        Page<SysOperationLog> page = new Page<>(1, 10, 1);
        page.setRecords(List.of(entity));
        when(mapper.selectPage(
                org.mockito.ArgumentMatchers.<Page<SysOperationLog>>any(),
                org.mockito.ArgumentMatchers.<Wrapper<SysOperationLog>>any()
        )).thenReturn(page);
        OperationLogQueryServiceImpl service = new OperationLogQueryServiceImpl(mapper);

        PageResult<OperationLogVO> result = service.getOperationLogPage(new OperationLogSearchDTO(), 1, 10);

        assertEquals(1L, result.getTotal());
        assertEquals("用户管理", result.getList().get(0).getModule());
    }

    @Test
    void getOperationLogPageShouldRejectInvalidTimeRange() {
        OperationLogSearchDTO dto = new OperationLogSearchDTO();
        dto.setStartTime(LocalDateTime.of(2026, 8, 5, 0, 0));
        dto.setEndTime(LocalDateTime.of(2026, 8, 4, 0, 0));
        OperationLogQueryServiceImpl service = new OperationLogQueryServiceImpl(mock(SysOperationLogMapper.class));

        assertThrows(BusinessException.class, () -> service.getOperationLogPage(dto, 1, 10));
    }
}
