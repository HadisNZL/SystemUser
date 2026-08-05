package com.system.filter;

import com.system.client.SystemAuthorizationClient;
import com.system.common.Result;
import com.system.vo.UserAuthorizationVO;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * 日志服务网关身份恢复测试。
 */
class GatewayForwardAuthenticationFilterTest {

    @Test
    void shouldRestorePermissionsFromSystemService() throws Exception {
        SystemAuthorizationClient client = (userId, source) -> {
            UserAuthorizationVO vo = new UserAuthorizationVO();
            vo.setUserId(userId);
            vo.setStatus(1);
            vo.setPermissionKeys(List.of("sys:log:list"));
            return Result.success(vo);
        };
        GatewayForwardAuthenticationFilter filter = new GatewayForwardAuthenticationFilter(client);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/log/operation/search_list");
        request.addHeader("X-Gateway-Forwarded", "true");
        request.addHeader("X-User-Id", "1");

        FilterChain chain = (servletRequest, servletResponse) -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            assertEquals("1", authentication.getName());
            assertEquals("sys:log:list", authentication.getAuthorities().iterator().next().getAuthority());
        };

        filter.doFilter(request, new MockHttpServletResponse(), chain);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}
