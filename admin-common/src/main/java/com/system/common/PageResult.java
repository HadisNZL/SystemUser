package com.system.common;

import java.util.List;

/**
 * 分页返回对象。
 */
public class PageResult<T> {

    private Long total;
    private List<T> list;

    public static <T> PageResult<T> build(Long total, List<T> list) {
        PageResult<T> pageResult = new PageResult<>();
        pageResult.setTotal(total);
        pageResult.setList(list);
        return pageResult;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }
}
