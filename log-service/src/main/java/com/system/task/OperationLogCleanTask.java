package com.system.task;

import com.system.service.OperationLogCleanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 定时清理历史操作日志。
 */
@Slf4j
@Component
public class OperationLogCleanTask {

    private final OperationLogCleanService operationLogCleanService;
    private final int retentionDays;

    public OperationLogCleanTask(OperationLogCleanService operationLogCleanService,
                                 @Value("${system.operation-log.retention-days:90}") int retentionDays) {
        this.operationLogCleanService = operationLogCleanService;
        this.retentionDays = retentionDays;
    }

    @Scheduled(cron = "${system.operation-log.clean-cron:0 0 3 * * ?}")
    public void cleanExpiredOperationLogs() {
        int count = operationLogCleanService.cleanExpiredLogs(retentionDays);
        if (count > 0) {
            log.info("清理历史操作日志完成，保留天数：{}，清理数量：{}", retentionDays, count);
        }
    }
}
