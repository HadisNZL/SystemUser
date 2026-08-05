package com.system.service.impl;

import com.system.entity.SysOperationLog;
import com.system.mapper.SysOperationLogMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 操作日志清理服务测试。
 */
class OperationLogCleanServiceImplTest {

    @Test
    void cleanExpiredLogsShouldDeleteHistory() {
        SysOperationLogMapper mapper = mock(SysOperationLogMapper.class);
        when(mapper.delete(any())).thenReturn(3);
        OperationLogCleanServiceImpl service = new OperationLogCleanServiceImpl(mapper);

        int count = service.cleanExpiredLogs(90);

        assertEquals(3, count);
        verify(mapper).delete(any());
    }

    @Test
    void cleanExpiredLogsShouldSkipInvalidRetentionDays() {
        SysOperationLogMapper mapper = mock(SysOperationLogMapper.class);
        OperationLogCleanServiceImpl service = new OperationLogCleanServiceImpl(mapper);

        assertEquals(0, service.cleanExpiredLogs(0));
        verify(mapper, never()).delete(any());
    }
}
