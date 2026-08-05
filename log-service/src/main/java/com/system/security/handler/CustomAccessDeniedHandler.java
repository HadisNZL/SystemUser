package com.system.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.system.common.Result;
import com.system.common.ResultCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 日志服务权限不足响应处理。
 */
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException)
            throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.getWriter().write(MAPPER.writeValueAsString(Result.fail(ResultCode.FORBIDDEN)));
    }
}
