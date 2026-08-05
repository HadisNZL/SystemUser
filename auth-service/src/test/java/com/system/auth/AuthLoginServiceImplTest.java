package com.system.auth;

import com.system.auth.client.SystemServiceAuthClient;
import com.system.auth.config.JwtProperties;
import com.system.auth.dto.AuthLoginDTO;
import com.system.auth.service.CaptchaValidateService;
import com.system.auth.service.impl.AuthLoginServiceImpl;
import com.system.auth.util.JwtUtil;
import com.system.auth.vo.LoginUserVO;
import com.system.common.BusinessException;
import com.system.common.Result;
import com.system.common.ResultCode;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AuthLoginServiceImplTest {

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Test
    void loginShouldGenerateTokenWhenPasswordMatches() {
        AuthLoginServiceImpl service = buildService("123456", 1);

        String token = service.login(buildLoginDTO("123456"));

        assertFalse(token.isBlank());
    }

    @Test
    void loginShouldThrowWhenPasswordNotMatches() {
        AuthLoginServiceImpl service = buildService("123456", 1);

        assertThrows(BusinessException.class, () -> service.login(buildLoginDTO("wrong-password")));
    }

    @Test
    void loginShouldThrowWhenUserDisabled() {
        AuthLoginServiceImpl service = buildService("123456", 0);

        assertThrows(BusinessException.class, () -> service.login(buildLoginDTO("123456")));
    }

    @Test
    void loginShouldPreserveServiceUnavailableResult() {
        SystemServiceAuthClient client = (username, internalSource) -> Result.fail(ResultCode.SERVICE_UNAVAILABLE);
        AuthLoginServiceImpl service = buildService(client);

        BusinessException exception = assertThrows(
                BusinessException.class, () -> service.login(buildLoginDTO("123456")));

        assertEquals(ResultCode.SERVICE_UNAVAILABLE.getCode(), exception.getCode());
    }

    private AuthLoginServiceImpl buildService(String rawPassword, Integer status) {
        SystemServiceAuthClient client = (username, internalSource) -> Result.success(buildLoginUser(rawPassword, status));
        return buildService(client);
    }

    private AuthLoginServiceImpl buildService(SystemServiceAuthClient client) {
        CaptchaValidateService captchaValidateService = (captchaKey, captchaCode) -> {
        };
        JwtProperties jwtProperties = new JwtProperties();
        jwtProperties.setSecret("abc123456abc123456abc123456abc123456");
        jwtProperties.setExpire(7200L);
        return new AuthLoginServiceImpl(client, captchaValidateService, passwordEncoder, new JwtUtil(jwtProperties));
    }

    private LoginUserVO buildLoginUser(String rawPassword, Integer status) {
        LoginUserVO loginUser = new LoginUserVO();
        loginUser.setId(1L);
        loginUser.setUsername("admin");
        loginUser.setPassword(passwordEncoder.encode(rawPassword));
        loginUser.setStatus(status);
        return loginUser;
    }

    private AuthLoginDTO buildLoginDTO(String password) {
        AuthLoginDTO loginDTO = new AuthLoginDTO();
        loginDTO.setUsername("admin");
        loginDTO.setPassword(password);
        loginDTO.setCaptchaKey("test-key");
        loginDTO.setCaptchaCode("A1B2");
        return loginDTO;
    }
}
