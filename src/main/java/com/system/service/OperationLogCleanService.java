package com.system.service;

/**
 * 操作日志清理服务。
 */
public interface OperationLogCleanService {

    int cleanExpiredLogs(int retentionDays);
}
