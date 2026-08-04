package com.system.filter;

import com.system.common.BusinessException;
import com.system.common.SystemConstants;
import com.system.service.TokenBlacklistService;
import com.system.util.JwtUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Security 核心组件
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String GATEWAY_FORWARDED_HEADER = "X-Gateway-Forwarded";
    private static final String USER_ID_HEADER = "X-User-Id";

    @Resource
    private JwtUtil jwtUtil;
    @Resource
    private UserDetailsService userDetailsService;
    @Resource
    private TokenBlacklistService tokenBlacklistService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader(SystemConstants.AUTHORIZATION_HEADER);
        // 网关已完成JWT校验时，下游服务从透传头恢复登录上下文。
        if (authHeader == null || !authHeader.startsWith(SystemConstants.BEARER_PREFIX)) {
            if (restoreGatewayAuthentication(request)) {
                filterChain.doFilter(request, response);
                return;
            }
        }

        // 无token直接放行，由Security后续判断是否需要认证
        if (authHeader == null || !authHeader.startsWith(SystemConstants.BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }
        String token = authHeader.substring(SystemConstants.BEARER_PREFIX_LENGTH);
        try {
            jwtUtil.validateToken(token);
            if (tokenBlacklistService.isBlacklisted(token)) {
                throw new BusinessException("登录已退出，请重新登录");
            }
            Long userId = jwtUtil.getUserId(token);
            // 根据userId加载用户信息与权限
            UserDetails userDetails = userDetailsService.loadUserByUsername(userId.toString());
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (BusinessException e) {
            // 抛出异常，由CustomAuthEntryPoint捕获统一返回401
            throw new BusinessException(e.getMessage());
        }
        filterChain.doFilter(request, response);
    }

    private boolean restoreGatewayAuthentication(HttpServletRequest request) {
        String gatewayForwarded = request.getHeader(GATEWAY_FORWARDED_HEADER);
        String userId = request.getHeader(USER_ID_HEADER);
        if (!"true".equalsIgnoreCase(gatewayForwarded) || userId == null || userId.isBlank()) {
            return false;
        }
        UserDetails userDetails = userDetailsService.loadUserByUsername(userId);
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return true;
    }
}
