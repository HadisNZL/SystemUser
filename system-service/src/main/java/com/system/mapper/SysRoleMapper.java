package com.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.system.entity.SysRole;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色数据访问接口。
 */
public interface SysRoleMapper extends BaseMapper<SysRole> {

    List<SysRole> selectRolesByUserId(@Param("userId") Long userId);
}
