package com.system.gateway.config;

import com.alibaba.csp.sentinel.adapter.gateway.common.SentinelGatewayConstants;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiDefinition;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiPathPredicateItem;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.GatewayApiDefinitionManager;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayFlowRule;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayRuleManager;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

/**
 * 网关核心接口Sentinel规则。
 */
@Configuration
@EnableConfigurationProperties(GatewaySentinelProperties.class)
public class GatewaySentinelConfig {

    static final String LOGIN_API = "auth-login-api";
    static final String USER_SEARCH_API = "system-user-search-api";
    static final String FILE_UPLOAD_API = "file-upload-api";

    private final GatewaySentinelProperties properties;

    public GatewaySentinelConfig(GatewaySentinelProperties properties) {
        this.properties = properties;
    }

    @PostConstruct
    public void initializeRules() {
        GatewayApiDefinitionManager.loadApiDefinitions(Set.of(
                api(LOGIN_API, "/auth/login"),
                api(USER_SEARCH_API, "/system/user/search_list"),
                api(FILE_UPLOAD_API, "/file/upload")
        ));
        GatewayRuleManager.loadRules(Set.of(
                flowRule(LOGIN_API, properties.getLoginQps()),
                flowRule(USER_SEARCH_API, properties.getUserSearchQps()),
                flowRule(FILE_UPLOAD_API, properties.getFileUploadQps())
        ));
    }

    private ApiDefinition api(String name, String path) {
        return new ApiDefinition(name).setPredicateItems(Set.of(
                new ApiPathPredicateItem().setPattern(path)
        ));
    }

    private GatewayFlowRule flowRule(String resource, double qps) {
        return new GatewayFlowRule(resource)
                .setResourceMode(SentinelGatewayConstants.RESOURCE_MODE_CUSTOM_API_NAME)
                .setGrade(RuleConstant.FLOW_GRADE_QPS)
                .setCount(qps)
                .setIntervalSec(1);
    }
}
