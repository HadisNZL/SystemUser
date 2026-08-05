package com.system.auth.service;

import com.system.auth.dto.AuthLoginDTO;

/**
 * 认证服务登录业务。
 */
public interface AuthLoginService {

    String login(AuthLoginDTO loginDTO);
}
