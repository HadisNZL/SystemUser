package com.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.system.entity.SysPermission;
import org.apache.ibatis.annotations.Param;

import java.util.List;

//@Mapper // 声明这是一个 MyBatis 的 Mapper，因为全局@MapperScan("com.system.mapper")配置了，则不需要每个都加了
public interface SysPermissionMapper extends BaseMapper<SysPermission> {
    /**
     * 根据用户ID查询该用户所有有效的权限标识集合
     */
    List<String> selectUserPermissionKeys(@Param("userId") Long userId);

    Long countRolePermissionByPermissionId(@Param("permissionId") Long permissionId);
}
