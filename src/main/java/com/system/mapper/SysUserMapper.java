package com.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.system.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper // 声明这是一个 MyBatis 的 Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
    // 继承 BaseMapper<SysUser> 后，自带 selectById、selectList 等方法
    // 手写物理删除，不经过 MyBatis-Plus 逻辑删除拦截
    //在对应SysUserMapper.xml手写即可
    int physicalDeleteById(@Param("id") Long id);
}
