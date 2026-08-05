package com.system.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 接收网关透传的用户信息。
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GatewayForwardAuthenticationFilter extends OncePerRequestFilter {

    private static final String GATEWAY_FORWARDED_HEADER = "X-Gateway-Forwarded";
    private static final String USER_ID_HEADER = "X-User-Id";

    private final UserDetailsService userDetailsService;

    public GatewayForwardAuthenticationFilter(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            if ("true".equalsIgnoreCase(request.getHeader(GATEWAY_FORWARDED_HEADER))) {
                String userId = request.getHeader(USER_ID_HEADER);
                if (userId != null && !userId.isBlank()) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(userId);
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
            filterChain.doFilter(request, response);
        } finally {
            SecurityContextHolder.clearContext();
        }
    }
}
