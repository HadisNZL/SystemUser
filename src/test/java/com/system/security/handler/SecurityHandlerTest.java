package com.system.security.handler;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;

import static org.assertj.core.api.Assertions.assertThat;

class SecurityHandlerTest {

    @Test
    void authEntryPointShouldReturnUnauthorizedResult() throws Exception {
        CustomAuthEntryPoint entryPoint = new CustomAuthEntryPoint();
        MockHttpServletResponse response = new MockHttpServletResponse();

        entryPoint.commence(new MockHttpServletRequest(), response, new BadCredentialsException("token invalid"));

        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getContentAsString()).contains("\"code\":401");
        assertThat(response.getContentAsString()).contains("\"isSuccess\":false");
    }

    @Test
    void accessDeniedHandlerShouldReturnForbiddenResult() throws Exception {
        CustomAccessDeniedHandler handler = new CustomAccessDeniedHandler();
        MockHttpServletResponse response = new MockHttpServletResponse();

        handler.handle(new MockHttpServletRequest(), response, new AccessDeniedException("forbidden"));

        assertThat(response.getStatus()).isEqualTo(403);
        assertThat(response.getContentAsString()).contains("\"code\":403");
        assertThat(response.getContentAsString()).contains("\"isSuccess\":false");
    }
}
