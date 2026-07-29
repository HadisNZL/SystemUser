package com.system.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.system.common.SecurityResult;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 403 权限不足处理器 AccessDeniedHandler
 */
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setContentType("application/json;charset=UTF-8");
        // 403 登录成功但是没有接口访问权限
        SecurityResult<Object> result = SecurityResult.fail(403, "没有访问该接口的权限");
        response.getWriter().write(MAPPER.writeValueAsString(result));
    }
}