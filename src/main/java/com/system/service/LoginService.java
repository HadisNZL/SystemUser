package com.system.service;

import com.system.dto.LoginDTO;

public interface LoginService {
    /**
     * 登录接口
     */
    String login(LoginDTO loginDTO);
}
