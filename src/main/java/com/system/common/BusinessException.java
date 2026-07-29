package com.system.common;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 自定义业务异常
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BusinessException extends RuntimeException {
    private Integer code;

    public BusinessException(Integer code, String msg) {
        super(msg);
        this.code = code;
    }

    public BusinessException(String msg) {
        super(msg);
        this.code = 500;
    }
}
