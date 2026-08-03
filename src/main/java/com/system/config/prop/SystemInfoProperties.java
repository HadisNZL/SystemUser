package com.system.config.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

/**
 * 系统展示信息配置，支持从Nacos配置中心刷新。
 */
@Data
@RefreshScope
@Component
@ConfigurationProperties(prefix = "system.info")
public class SystemInfoProperties {

    private String name = "admin-system";

    private String description = "企业级后台管理系统";

    private String version = "0.0.1";
}
