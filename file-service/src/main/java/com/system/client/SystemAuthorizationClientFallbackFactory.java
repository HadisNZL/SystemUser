package com.system.client;

import com.system.common.Result;
import com.system.common.ResultCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * system-service授权查询降级。
 */
@Component
public class SystemAuthorizationClientFallbackFactory implements FallbackFactory<SystemAuthorizationClient> {

    private static final Logger log = LoggerFactory.getLogger(SystemAuthorizationClientFallbackFactory.class);

    @Override
    public SystemAuthorizationClient create(Throwable cause) {
        log.error("调用system-service授权接口失败", cause);
        return (userId, internalSource) -> Result.fail(ResultCode.SERVICE_UNAVAILABLE);
    }
}
