package com.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.system.entity.SysUser;

/**
 * 用户 Mapper。
 */
public interface SysUserMapper extends BaseMapper<SysUser> {

    int physicalDeleteById(Long id);
}
