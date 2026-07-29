package com.system.common;

/**
 * 系统全局常量
 */
public class SystemConstants {

    // ===================== 用户状态 =====================
    /**
     * 正常启用
     */
    public static final Integer USER_NORMAL = 1;
    /**
     * 禁用
     */
    public static final Integer USER_DISABLE = 0;

    // ===================== 逻辑删除 =====================
    /**
     * 未删除
     */
    public static final Integer NOT_DELETE = 0;
    /**
     * 已删除
     */
    public static final Integer DELETED = 1;

    // ===================== 分页默认值 =====================
    public static final String DEFAULT_PAGE_NUM = "1";
    public static final String DEFAULT_PAGE_SIZE = "10";

    // ===================== 乐观锁提示 =====================
    public static final String OPTIMISTIC_LOCK_MSG = "数据已被其他用户修改，请刷新后重试";

    // ===================== Token相关 =====================
    public static final String TOKEN_HEADER_NAME = "token";


    // JWT规范
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";
    public static final int BEARER_PREFIX_LENGTH = BEARER_PREFIX.length();
}