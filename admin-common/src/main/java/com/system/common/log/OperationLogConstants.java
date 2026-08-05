package com.system.common.log;

/**
 * 操作日志消息队列常量。
 */
public final class OperationLogConstants {

    public static final String EXCHANGE = "admin.operation.log.exchange";
    public static final String QUEUE = "admin.operation.log.queue";
    public static final String ROUTING_KEY = "admin.operation.log";

    private OperationLogConstants() {
    }
}
