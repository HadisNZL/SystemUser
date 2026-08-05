package com.system.service;

import com.system.vo.InternalLoginUserVO;

/**
 * 提供登录所需的内部用户查询。
 */
public interface InternalAuthService {

    InternalLoginUserVO getLoginUser(String username);
}
