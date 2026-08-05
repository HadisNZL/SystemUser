package com.system.auth.service.impl;

import com.system.auth.client.SystemServiceAuthClient;
import com.system.auth.dto.AuthLoginDTO;
import com.system.auth.service.AuthLoginService;
import com.system.auth.service.CaptchaValidateService;
import com.system.auth.util.JwtUtil;
import com.system.auth.vo.LoginUserVO;
import com.system.common.BusinessException;
import com.system.common.Result;
import com.system.common.ResultCode;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 认证服务登录实现。
 */
@Service
public class AuthLoginServiceImpl implements AuthLoginService {

    private static final Integer USER_DISABLE = 0;

    private final SystemServiceAuthClient systemServiceAuthClient;
    private final CaptchaValidateService captchaValidateService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthLoginServiceImpl(SystemServiceAuthClient systemServiceAuthClient,
                                CaptchaValidateService captchaValidateService,
                                PasswordEncoder passwordEncoder,
                                JwtUtil jwtUtil) {
        this.systemServiceAuthClient = systemServiceAuthClient;
        this.captchaValidateService = captchaValidateService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public String login(AuthLoginDTO loginDTO) {
        captchaValidateService.validateCaptcha(loginDTO.getCaptchaKey(), loginDTO.getCaptchaCode());
        LoginUserVO loginUser = getLoginUser(loginDTO.getUsername());
        if (USER_DISABLE.equals(loginUser.getStatus())) {
            throw new BusinessException(ResultCode.USER_DISABLED);
        }
        if (!passwordEncoder.matches(loginDTO.getPassword(), loginUser.getPassword())) {
            throw new BusinessException(ResultCode.LOGIN_FAIL);
        }
        return jwtUtil.generateToken(loginUser.getId());
    }

    private LoginUserVO getLoginUser(String username) {
        Result<LoginUserVO> result = systemServiceAuthClient.getLoginUser(username, SystemServiceAuthClient.INTERNAL_SOURCE_VALUE);
        if (result != null && ResultCode.SERVICE_UNAVAILABLE.getCode().equals(result.getCode())) {
            throw new BusinessException(ResultCode.SERVICE_UNAVAILABLE);
        }
        if (result == null || !Boolean.TRUE.equals(result.getIsSuccess()) || result.getData() == null) {
            throw new BusinessException(ResultCode.LOGIN_FAIL);
        }
        return result.getData();
    }
}
