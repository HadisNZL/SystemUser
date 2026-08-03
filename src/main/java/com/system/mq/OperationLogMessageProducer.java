package com.system.mq;

import com.system.config.OperationLogRabbitConfig;
import com.system.entity.SysOperationLog;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * 操作日志消息生产者。
 */
@Slf4j
@Component
public class OperationLogMessageProducer {

    @Resource
    private RabbitTemplate rabbitTemplate;

    public void send(SysOperationLog operationLog) {
        try {
            rabbitTemplate.convertAndSend(
                    OperationLogRabbitConfig.OPERATION_LOG_EXCHANGE,
                    OperationLogRabbitConfig.OPERATION_LOG_ROUTING_KEY,
                    operationLog
            );
        } catch (Exception e) {
            log.error("发送操作日志消息失败", e);
        }
    }
}
