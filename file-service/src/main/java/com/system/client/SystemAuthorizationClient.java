package com.system.client;

import com.system.common.Result;
import com.system.vo.UserAuthorizationVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 调用system-service内部授权接口。
 */
@FeignClient(contextId = "fileSystemAuthorizationClient", name = "system-service", path = "/internal/authorization")
public interface SystemAuthorizationClient {

    String INTERNAL_SOURCE_HEADER = "X-Internal-Source";
    String INTERNAL_SOURCE_VALUE = "file-service";

    @GetMapping("/user")
    Result<UserAuthorizationVO> getUserAuthorization(
            @RequestParam("userId") Long userId,
            @RequestHeader(INTERNAL_SOURCE_HEADER) String internalSource);
}
