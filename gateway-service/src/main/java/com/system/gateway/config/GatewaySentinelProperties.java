package com.system.gateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 网关核心接口限流阈值。
 */
@ConfigurationProperties(prefix = "system.sentinel")
public class GatewaySentinelProperties {

    private double loginQps = 5;
    private double userSearchQps = 20;
    private double fileUploadQps = 3;

    public double getLoginQps() {
        return loginQps;
    }

    public void setLoginQps(double loginQps) {
        this.loginQps = loginQps;
    }

    public double getUserSearchQps() {
        return userSearchQps;
    }

    public void setUserSearchQps(double userSearchQps) {
        this.userSearchQps = userSearchQps;
    }

    public double getFileUploadQps() {
        return fileUploadQps;
    }

    public void setFileUploadQps(double fileUploadQps) {
        this.fileUploadQps = fileUploadQps;
    }
}
