package com.system.service;

import com.system.common.PageResult;
import com.system.dto.UserSearchDTO;
import com.system.entity.SysUser;
import com.system.vo.UserPageVO;

import java.util.List;

public interface SysUserService {

    List<SysUser> findUserList();

    //  多条件查询
    PageResult<UserPageVO> getUserPage(UserSearchDTO dto, Integer pageNum, Integer pageSize);

    // 插入一条数据

    boolean saveUser(UserPageVO user);

    /**
     * 删除一条数据
     * MyBatis-Plus操作 deleteById(id)
     * 分两种情况
     * 1.表里面增加了@TableLogic标识
     * 则 执行逻辑删除
     * MyBatis-Plus 在框架底层做了全局拦截。当你调用 deleteById(id) 时，
     * 它会瞬间“变脸”，把原本要执行的 DELETE 语句，强行篡改并转换成一条 UPDATE 更新语句
     * 2.表里面没有@TableLogic标识
     * 则 执行物理删除
     * MyBatis-Plus 认准了这就是个普通字段，它在底层生成的 SQL 就是硬碰硬的物理删除
     */
    boolean deleteUser(Long id);

    /**
     * 管理员临时 物理删除，不要外泄
     */
    boolean adminPhysicalDeleteUser(Long id);
}
