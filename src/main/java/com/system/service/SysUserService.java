package com.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.system.entity.SysUser;

import java.util.List;

public interface SysUserService {
    List<SysUser> findUserList();

    // 返回 MyBatis-Plus 自带的 Page 分页对象
    Page<SysUser> getUserPage(Integer pageNum, Integer pageSize);
}
