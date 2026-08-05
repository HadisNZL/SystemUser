package com.system.common;

/**
 * 统一响应码。
 */
public enum ResultCode {

    SUCCESS(200, "操作成功"),
    BAD_REQUEST(400, "参数错误"),
    UNAUTHORIZED(401, "未登录或登录已失效"),
    FORBIDDEN(403, "没有访问该接口的权限"),
    RATE_LIMIT(429, "请求过于频繁，请稍后再试"),
    SYSTEM_ERROR(500, "系统异常，请联系管理员"),
    BUSINESS_ERROR(10001, "业务处理失败"),
    LOGIN_FAIL(10002, "账号或密码错误"),
    USER_DISABLED(10003, "账号已被禁用"),
    DATA_NOT_FOUND(10004, "数据不存在"),
    OPTIMISTIC_LOCK_ERROR(10005, "数据已被其他用户修改，请刷新后重试");

    private final Integer code;
    private final String msg;

    ResultCode(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
