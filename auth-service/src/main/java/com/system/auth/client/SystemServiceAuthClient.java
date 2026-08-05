package com.system.auth.client;

import com.system.auth.vo.LoginUserVO;
import com.system.common.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 调用system-service内部认证接口。
 */
@FeignClient(contextId = "systemServiceAuthClient", name = "system-service", path = "/internal/auth")
public interface SystemServiceAuthClient {

    String INTERNAL_SOURCE_HEADER = "X-Internal-Source";
    String INTERNAL_SOURCE_VALUE = "auth-service";

    @GetMapping("/login-user")
    Result<LoginUserVO> getLoginUser(@RequestParam("username") String username,
                                     @RequestHeader(INTERNAL_SOURCE_HEADER) String internalSource);
}
