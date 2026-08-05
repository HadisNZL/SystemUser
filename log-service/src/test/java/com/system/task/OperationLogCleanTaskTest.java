package com.system.task;

import com.system.service.OperationLogCleanService;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 操作日志定时任务测试。
 */
class OperationLogCleanTaskTest {

    @Test
    void cleanExpiredOperationLogsShouldCallService() {
        OperationLogCleanService service = mock(OperationLogCleanService.class);
        when(service.cleanExpiredLogs(90)).thenReturn(2);
        OperationLogCleanTask task = new OperationLogCleanTask(service, 90);

        task.cleanExpiredOperationLogs();

        verify(service).cleanExpiredLogs(90);
    }
}
