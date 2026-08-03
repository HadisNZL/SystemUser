package com.system.task;

import com.system.service.OperationLogCleanService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 操作日志定时清理任务测试。
 */
class OperationLogCleanTaskTest {

    private OperationLogCleanTask operationLogCleanTask;
    private OperationLogCleanService operationLogCleanService;

    @BeforeEach
    void setUp() {
        operationLogCleanTask = new OperationLogCleanTask();
        operationLogCleanService = mock(OperationLogCleanService.class);
        ReflectionTestUtils.setField(operationLogCleanTask, "operationLogCleanService", operationLogCleanService);
        ReflectionTestUtils.setField(operationLogCleanTask, "retentionDays", 90);
    }

    @Test
    void cleanExpiredOperationLogsShouldCallCleanService() {
        when(operationLogCleanService.cleanExpiredLogs(90)).thenReturn(2);

        operationLogCleanTask.cleanExpiredOperationLogs();

        verify(operationLogCleanService).cleanExpiredLogs(90);
    }
}
