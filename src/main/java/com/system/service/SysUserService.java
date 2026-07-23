package com.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.system.common.PageResult;
import com.system.dto.UserSearchDTO;
import com.system.entity.SysUser;

import java.util.List;

public interface SysUserService {

    List<SysUser> findUserList();

    // 返回 MyBatis-Plus 自带的 Page 分页对象
    PageResult<SysUser> getUserPage(Integer pageNum, Integer pageSize);

    //  多条件查询
    PageResult<SysUser> searchUserPage(UserSearchDTO dto, Integer pageNum, Integer pageSize);

    // 插入一条数据

    boolean saveUser(SysUser user);
    //删除一条数据

    boolean deleteUserById(Long id);
}
