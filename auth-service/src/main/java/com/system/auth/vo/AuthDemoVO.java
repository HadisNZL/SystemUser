package com.system.auth.vo;

/**
 * OpenFeign演示响应对象。
 */
public record AuthDemoVO(String serviceName, String upstreamMessage) {
}
