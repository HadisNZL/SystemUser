package com.system.task;

import com.system.service.OperationLogCleanService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 操作日志定时清理任务。
 */
@Slf4j
@Component
public class OperationLogCleanTask {

    @Value("${system.operation-log.retention-days:90}")
    private int retentionDays;

    @Resource
    private OperationLogCleanService operationLogCleanService;

    @Scheduled(cron = "${system.operation-log.clean-cron:0 0 3 * * ?}")
    public void cleanExpiredOperationLogs() {
        int count = operationLogCleanService.cleanExpiredLogs(retentionDays);
        if (count > 0) {
            log.info("清理历史操作日志完成，保留天数：{}，清理数量：{}", retentionDays, count);
        }
    }
}
