package com.system.filter;

import com.system.client.SystemAuthorizationClient;
import com.system.common.Result;
import com.system.vo.UserAuthorizationVO;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * 根据网关用户ID恢复文件服务登录上下文。
 */
@Component
public class GatewayForwardAuthenticationFilter extends OncePerRequestFilter {

    private static final String GATEWAY_FORWARDED_HEADER = "X-Gateway-Forwarded";
    private static final String USER_ID_HEADER = "X-User-Id";
    private static final Integer USER_DISABLE = 0;

    private final SystemAuthorizationClient systemAuthorizationClient;

    public GatewayForwardAuthenticationFilter(SystemAuthorizationClient systemAuthorizationClient) {
        this.systemAuthorizationClient = systemAuthorizationClient;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            restoreAuthentication(request);
            filterChain.doFilter(request, response);
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    private void restoreAuthentication(HttpServletRequest request) {
        if (!"true".equalsIgnoreCase(request.getHeader(GATEWAY_FORWARDED_HEADER))) {
            return;
        }
        String userId = request.getHeader(USER_ID_HEADER);
        if (userId == null || userId.isBlank()) {
            return;
        }
        try {
            Result<UserAuthorizationVO> result = systemAuthorizationClient.getUserAuthorization(
                    Long.valueOf(userId), SystemAuthorizationClient.INTERNAL_SOURCE_VALUE);
            if (result == null || !Boolean.TRUE.equals(result.getIsSuccess()) || result.getData() == null
                    || USER_DISABLE.equals(result.getData().getStatus())) {
                return;
            }
            List<SimpleGrantedAuthority> authorities = result.getData().getPermissionKeys() == null
                    ? List.of()
                    : result.getData().getPermissionKeys().stream().map(SimpleGrantedAuthority::new).toList();
            UserDetails userDetails = new User(userId, "", authorities);
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (RuntimeException ignored) {
            SecurityContextHolder.clearContext();
        }
    }
}
