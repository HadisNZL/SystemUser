package com.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.system.entity.SysOperationLog;
import com.system.mapper.SysOperationLogMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 操作日志清理服务测试。
 */
@SuppressWarnings("unchecked")
class OperationLogCleanServiceImplTest {

    private OperationLogCleanServiceImpl operationLogCleanService;
    private SysOperationLogMapper sysOperationLogMapper;

    @BeforeEach
    void setUp() {
        operationLogCleanService = new OperationLogCleanServiceImpl();
        sysOperationLogMapper = mock(SysOperationLogMapper.class);
        ReflectionTestUtils.setField(operationLogCleanService, "sysOperationLogMapper", sysOperationLogMapper);
    }

    @Test
    void cleanExpiredLogsShouldDeleteLogsBeforeRetentionDays() {
        when(sysOperationLogMapper.delete(any(Wrapper.class))).thenReturn(3);

        int count = operationLogCleanService.cleanExpiredLogs(90);

        assertEquals(3, count);
        verify(sysOperationLogMapper).delete(any(Wrapper.class));
    }

    @Test
    void cleanExpiredLogsShouldSkipWhenRetentionDaysInvalid() {
        int count = operationLogCleanService.cleanExpiredLogs(0);

        assertEquals(0, count);
        verify(sysOperationLogMapper, never()).delete(any(Wrapper.class));
    }
}
