package com.system.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.system.common.SecurityResult;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 401 未登录处理器 AuthenticationEntryPoint
 */
@Component
public class CustomAuthEntryPoint implements AuthenticationEntryPoint {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        response.setContentType("application/json;charset=UTF-8");
        // 401 未登录 / token非法/过期
        SecurityResult<Object> result = SecurityResult.fail(401, "登录已失效，请重新登录");
        response.getWriter().write(MAPPER.writeValueAsString(result));
    }
}