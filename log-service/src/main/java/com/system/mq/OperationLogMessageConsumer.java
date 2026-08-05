package com.system.mq;

import com.system.common.log.OperationLogConstants;
import com.system.common.log.OperationLogEvent;
import com.system.entity.SysOperationLog;
import com.system.mapper.SysOperationLogMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 消费操作日志消息并写入数据库。
 */
@Slf4j
@Component
public class OperationLogMessageConsumer {

    private final SysOperationLogMapper sysOperationLogMapper;

    public OperationLogMessageConsumer(SysOperationLogMapper sysOperationLogMapper) {
        this.sysOperationLogMapper = sysOperationLogMapper;
    }

    @RabbitListener(queues = OperationLogConstants.QUEUE)
    public void consume(OperationLogEvent event) {
        try {
            sysOperationLogMapper.insert(toEntity(event));
        } catch (Exception e) {
            log.error("异步保存操作日志失败", e);
        }
    }

    private SysOperationLog toEntity(OperationLogEvent event) {
        SysOperationLog entity = new SysOperationLog();
        entity.setModule(event.getModule());
        entity.setOperation(event.getOperation());
        entity.setRequestMethod(event.getRequestMethod());
        entity.setRequestUri(event.getRequestUri());
        entity.setRequestParams(event.getRequestParams());
        entity.setResponseResult(event.getResponseResult());
        entity.setStatus(event.getStatus());
        entity.setErrorMsg(event.getErrorMsg());
        entity.setOperatorId(event.getOperatorId());
        entity.setIp(event.getIp());
        entity.setCostTime(event.getCostTime());
        entity.setCreateTime(event.getCreateTime());
        return entity;
    }
}
