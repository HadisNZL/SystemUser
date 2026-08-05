package com.system.common;

/**
 * 自定义业务异常。
 */
public class BusinessException extends RuntimeException {

    private final Integer code;

    public BusinessException(Integer code, String msg) {
        super(msg);
        this.code = code;
    }

    public BusinessException(ResultCode resultCode) {
        super(resultCode.getMsg());
        this.code = resultCode.getCode();
    }

    public BusinessException(ResultCode resultCode, String msg) {
        super(msg);
        this.code = resultCode.getCode();
    }

    public BusinessException(String msg) {
        super(msg);
        this.code = ResultCode.BUSINESS_ERROR.getCode();
    }

    public Integer getCode() {
        return code;
    }
}
