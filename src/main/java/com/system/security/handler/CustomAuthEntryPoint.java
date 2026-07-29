package com.system.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.system.common.Result;
import com.system.common.ResultCode;
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
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        // 401 未登录 / token非法/过期
        Result<Object> result = Result.fail(ResultCode.UNAUTHORIZED);
        response.getWriter().write(MAPPER.writeValueAsString(result));
    }
}
