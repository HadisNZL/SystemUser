package com.system.auth.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 调用admin-system内部演示接口。
 */
@FeignClient(name = "admin-system", path = "/internal/demo")
public interface AdminSystemDemoClient {

    @GetMapping("/ping")
    String ping();
}
