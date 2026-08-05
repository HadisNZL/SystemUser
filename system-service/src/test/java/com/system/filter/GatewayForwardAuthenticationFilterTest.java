package com.system.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class GatewayForwardAuthenticationFilterTest {

    @Test
    void shouldRestoreAuthenticationFromGatewayHeaders() throws ServletException, IOException {
        UserDetailsService userDetailsService = userId -> new User(userId, "", List.of(new SimpleGrantedAuthority("sys:user:list")));
        GatewayForwardAuthenticationFilter filter = new GatewayForwardAuthenticationFilter(userDetailsService);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/system/menu/current");
        request.addHeader("X-Gateway-Forwarded", "true");
        request.addHeader("X-User-Id", "1001");
        MockHttpServletResponse response = new MockHttpServletResponse();

        FilterChain chain = new FilterChain() {
            @Override
            public void doFilter(jakarta.servlet.ServletRequest servletRequest, jakarta.servlet.ServletResponse servletResponse) {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                assertEquals("1001", authentication.getName());
                assertEquals("sys:user:list", authentication.getAuthorities().iterator().next().getAuthority());
            }
        };

        filter.doFilter(request, response, chain);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}
