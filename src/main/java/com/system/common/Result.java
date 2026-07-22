package com.system.common;

import lombok.Data;

/**
 * 封装通用 JSON 返回类
 * @param <T>
 */
@Data
public class Result<T> {
    // 状态码 200成功 500失败
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
        r.setCode(200);
        r.setMsg("操作成功");
        return r;
    }

    // 成功带数据
    public static <T> Result<T> success(T data) {
        Result<T> r = new Result<>();
        r.isSuccess = true;
        r.setCode(200);
        r.setMsg("操作成功");
        r.setData(data);
        return r;
    }

    // 失败
    public static <T> Result<T> fail(String msg) {
        Result<T> r = new Result<>();
        r.isSuccess = true;
        r.setCode(500);
        r.setMsg(msg);
        return r;
    }
}