package com.system.mq;

import com.system.common.log.OperationLogEvent;
import com.system.entity.SysOperationLog;
import com.system.mapper.SysOperationLogMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * 操作日志消费者测试。
 */
class OperationLogMessageConsumerTest {

    @Test
    void consumeShouldConvertEventAndInsertLog() {
        SysOperationLogMapper mapper = mock(SysOperationLogMapper.class);
        OperationLogMessageConsumer consumer = new OperationLogMessageConsumer(mapper);
        OperationLogEvent event = new OperationLogEvent();
        event.setModule("用户管理");
        event.setOperation("新增用户");
        event.setStatus(1);
        event.setOperatorId(1L);
        event.setCreateTime(LocalDateTime.of(2026, 8, 4, 16, 0));

        consumer.consume(event);

        ArgumentCaptor<SysOperationLog> captor = ArgumentCaptor.forClass(SysOperationLog.class);
        verify(mapper).insert(captor.capture());
        assertEquals("用户管理", captor.getValue().getModule());
        assertEquals("新增用户", captor.getValue().getOperation());
        assertEquals(1L, captor.getValue().getOperatorId());
    }
}
