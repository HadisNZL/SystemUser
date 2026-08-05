package com.system.util;

import com.system.common.BusinessException;
import com.system.common.ResultCode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 当前登录用户工具。
 */
public final class SecurityUtil {

    private SecurityUtil() {
    }

    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        return Long.valueOf(authentication.getName());
    }
}
