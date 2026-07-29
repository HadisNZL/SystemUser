package com.system.common;

import lombok.Data;

/**
 * 封装通用 JSON 返回类
 *
 * @param <T>
 */
@Data
public class Result<T> {
    // 状态码
    private Integer code;
    // 提示信息
    private String msg;
    private Boolean isSuccess;
    // 返回数据
    private T data;

    // 成功不带数据
    public static <T> Result<T> success() {
        Result<T> r = new Result<>();
        r.isSuccess = true;
        r.setCode(ResultCode.SUCCESS.getCode());
        r.setMsg(ResultCode.SUCCESS.getMsg());
        return r;
    }

    // 成功带数据
    public static <T> Result<T> success(T data) {
        Result<T> r = new Result<>();
        r.isSuccess = true;
        r.setCode(ResultCode.SUCCESS.getCode());
        r.setMsg(ResultCode.SUCCESS.getMsg());
        r.setData(data);
        return r;
    }

    // 失败
    public static <T> Result<T> fail(String msg) {
        return fail(ResultCode.BUSINESS_ERROR.getCode(), msg);
    }

    public static <T> Result<T> fail(ResultCode resultCode) {
        return fail(resultCode.getCode(), resultCode.getMsg());
    }

    public static <T> Result<T> fail(ResultCode resultCode, String msg) {
        return fail(resultCode.getCode(), msg);
    }

    public static <T> Result<T> fail(Integer code, String msg) {
        Result<T> r = new Result<>();
        r.isSuccess = false;
        r.setCode(code);
        r.setMsg(msg);
        return r;
    }
}
