package com.system.service;

import com.system.vo.InternalUserAuthorizationVO;

/**
 * 提供内部服务所需的用户授权信息。
 */
public interface InternalAuthorizationService {

    InternalUserAuthorizationVO getUserAuthorization(Long userId);
}
