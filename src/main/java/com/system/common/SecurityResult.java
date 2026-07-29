package com.system.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 安全异常返回工具
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SecurityResult<T> {
    private Integer code;
    private String msg;
    private T data;

    public static <T> SecurityResult<T> fail(Integer code, String msg) {
        return new SecurityResult<>(code, msg, null);
    }
}