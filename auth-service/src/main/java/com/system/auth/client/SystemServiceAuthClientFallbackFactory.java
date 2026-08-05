package com.system.auth.client;

import com.system.common.Result;
import com.system.common.ResultCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * system-service认证查询降级。
 */
@Component
public class SystemServiceAuthClientFallbackFactory implements FallbackFactory<SystemServiceAuthClient> {

    private static final Logger log = LoggerFactory.getLogger(SystemServiceAuthClientFallbackFactory.class);

    @Override
    public SystemServiceAuthClient create(Throwable cause) {
        log.error("调用system-service认证接口失败", cause);
        return (username, internalSource) -> Result.fail(ResultCode.SERVICE_UNAVAILABLE);
    }
}
