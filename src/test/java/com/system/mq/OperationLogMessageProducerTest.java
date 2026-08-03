package com.system.mq;

import com.system.config.OperationLogRabbitConfig;
import com.system.entity.SysOperationLog;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * 操作日志消息生产者测试。
 */
class OperationLogMessageProducerTest {

    private OperationLogMessageProducer producer;
    private RabbitTemplate rabbitTemplate;

    @BeforeEach
    void setUp() {
        producer = new OperationLogMessageProducer();
        rabbitTemplate = mock(RabbitTemplate.class);
        ReflectionTestUtils.setField(producer, "rabbitTemplate", rabbitTemplate);
    }

    @Test
    void sendShouldPublishOperationLogMessage() {
        SysOperationLog operationLog = new SysOperationLog();

        producer.send(operationLog);

        verify(rabbitTemplate).convertAndSend(
                OperationLogRabbitConfig.OPERATION_LOG_EXCHANGE,
                OperationLogRabbitConfig.OPERATION_LOG_ROUTING_KEY,
                operationLog
        );
    }
}
