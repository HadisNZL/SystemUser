package com.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.system.entity.SysPermission;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 权限数据访问接口。
 */
public interface SysPermissionMapper extends BaseMapper<SysPermission> {
    /**
     * 根据用户ID查询该用户所有有效的权限标识集合
     */
    List<String> selectUserPermissionKeys(@Param("userId") Long userId);

    Long countRolePermissionByPermissionId(@Param("permissionId") Long permissionId);

    List<SysPermission> selectPermissionsByRoleId(@Param("roleId") Long roleId);

    List<SysPermission> selectCurrentUserMenus(@Param("userId") Long userId);
}
