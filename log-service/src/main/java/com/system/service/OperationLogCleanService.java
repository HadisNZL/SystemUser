package com.system.service;

/**
 * 清理过期操作日志。
 */
public interface OperationLogCleanService {

    int cleanExpiredLogs(int retentionDays);
}
