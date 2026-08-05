package com.system.mq;

import com.system.common.log.OperationLogConstants;
import com.system.common.log.OperationLogEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * 发送操作日志消息。
 */
@Component
public class OperationLogMessageProducer {

    private static final Logger log = LoggerFactory.getLogger(OperationLogMessageProducer.class);

    private final RabbitTemplate rabbitTemplate;

    public OperationLogMessageProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void send(OperationLogEvent event) {
        try {
            rabbitTemplate.convertAndSend(OperationLogConstants.EXCHANGE, OperationLogConstants.ROUTING_KEY, event);
        } catch (Exception e) {
            log.error("发送操作日志消息失败", e);
        }
    }
}
