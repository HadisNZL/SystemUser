package com.system.gateway.config;

import com.alibaba.csp.sentinel.adapter.gateway.common.api.GatewayApiDefinitionManager;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayRuleManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GatewaySentinelConfigTest {

    static {
        System.setProperty("csp.sentinel.log.dir", "target/sentinel-logs");
    }

    @Test
    void shouldRegisterCoreApiRules() {
        GatewaySentinelProperties properties = new GatewaySentinelProperties();
        properties.setLoginQps(4);
        properties.setUserSearchQps(12);
        properties.setFileUploadQps(2);

        new GatewaySentinelConfig(properties).initializeRules();

        assertNotNull(GatewayApiDefinitionManager.getApiDefinition(GatewaySentinelConfig.LOGIN_API));
        assertEquals(4, GatewayRuleManager.getRulesForResource(GatewaySentinelConfig.LOGIN_API)
                .iterator().next().getCount());
        assertEquals(12, GatewayRuleManager.getRulesForResource(GatewaySentinelConfig.USER_SEARCH_API)
                .iterator().next().getCount());
        assertEquals(2, GatewayRuleManager.getRulesForResource(GatewaySentinelConfig.FILE_UPLOAD_API)
                .iterator().next().getCount());
    }
}
