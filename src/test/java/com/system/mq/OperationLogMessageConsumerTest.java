package com.system.mq;

import com.system.entity.SysOperationLog;
import com.system.mapper.SysOperationLogMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * 操作日志消息消费者测试。
 */
class OperationLogMessageConsumerTest {

    private OperationLogMessageConsumer consumer;
    private SysOperationLogMapper sysOperationLogMapper;

    @BeforeEach
    void setUp() {
        consumer = new OperationLogMessageConsumer();
        sysOperationLogMapper = mock(SysOperationLogMapper.class);
        ReflectionTestUtils.setField(consumer, "sysOperationLogMapper", sysOperationLogMapper);
    }

    @Test
    void consumeShouldSaveOperationLog() {
        SysOperationLog operationLog = new SysOperationLog();

        consumer.consume(operationLog);

        verify(sysOperationLogMapper).insert(operationLog);
    }
}
