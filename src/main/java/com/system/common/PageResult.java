package com.system.common;

import lombok.Data;

import java.util.List;

/**
 * 封装分页工具类
 *
 * @param <T>
 */
@Data
public class PageResult<T> {
    private Long total;     // 总条数
    private List<T> list;    // 当前页数据

    public static <T> PageResult<T> build(Long total, List<T> data) {
        PageResult<T> page = new PageResult<>();
        page.setTotal(total);
        page.setList(data);
        return page;
    }
}
