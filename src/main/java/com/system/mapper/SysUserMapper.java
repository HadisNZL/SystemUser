package com.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.system.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper // 声明这是一个 MyBatis 的 Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
    // 继承 BaseMapper<SysUser> 后，自带 selectById、selectList 等方法
}
