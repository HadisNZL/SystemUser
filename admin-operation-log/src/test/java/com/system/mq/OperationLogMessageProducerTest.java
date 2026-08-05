package com.system.mq;

import com.system.common.log.OperationLogConstants;
import com.system.common.log.OperationLogEvent;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * 操作日志生产者测试。
 */
class OperationLogMessageProducerTest {

    @Test
    void sendShouldPublishOperationLogEvent() {
        RabbitTemplate rabbitTemplate = mock(RabbitTemplate.class);
        OperationLogMessageProducer producer = new OperationLogMessageProducer(rabbitTemplate);
        OperationLogEvent event = new OperationLogEvent();

        producer.send(event);

        verify(rabbitTemplate).convertAndSend(
                OperationLogConstants.EXCHANGE,
                OperationLogConstants.ROUTING_KEY,
                event
        );
    }
}
