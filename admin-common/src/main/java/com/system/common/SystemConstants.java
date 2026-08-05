package com.system.common;

/**
 * 系统常量。
 */
public final class SystemConstants {

    private SystemConstants() {
    }

    public static final Integer USER_NORMAL = 1;
    public static final Integer USER_DISABLE = 0;
    public static final Integer NOT_DELETE = 0;
    public static final Integer DELETED = 1;
    public static final String DEFAULT_PAGE_NUM = "1";
    public static final String DEFAULT_PAGE_SIZE = "10";
    public static final String OPTIMISTIC_LOCK_MSG = "数据已被其他用户修改，请刷新后重试";
    public static final String TOKEN_HEADER_NAME = "token";
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";
    public static final int BEARER_PREFIX_LENGTH = BEARER_PREFIX.length();
}
