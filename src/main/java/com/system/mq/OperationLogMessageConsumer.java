package com.system.mq;

import com.system.config.OperationLogRabbitConfig;
import com.system.entity.SysOperationLog;
import com.system.mapper.SysOperationLogMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 操作日志消息消费者。
 */
@Slf4j
@Component
public class OperationLogMessageConsumer {

    @Resource
    private SysOperationLogMapper sysOperationLogMapper;

    @RabbitListener(queues = OperationLogRabbitConfig.OPERATION_LOG_QUEUE)
    public void consume(SysOperationLog operationLog) {
        try {
            sysOperationLogMapper.insert(operationLog);
        } catch (Exception e) {
            log.error("异步保存操作日志失败", e);
        }
    }
}
