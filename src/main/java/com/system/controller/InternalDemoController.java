package com.system.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 内部演示接口，供其他服务通过OpenFeign调用。
 */
@RestController
@RequestMapping("/internal/demo")
public class InternalDemoController {

    @GetMapping("/ping")
    public String ping() {
        return "pong from admin-system";
    }
}
