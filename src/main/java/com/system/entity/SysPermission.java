package com.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 权限表设计
 * 系统管理（目录）
 * ↓
 * 用户管理（菜单）
 * ↓
 * 查看用户（按钮）
 * 新增用户（按钮）
 * 修改用户（按钮）
 * 删除用户（按钮）
 * 物理删除（按钮）
 * <p>
 * <p>
 * 执行成功后你的权限树就是：
 * <p>
 * 系统管理(type=1)
 * └── 用户管理(type=2)
 * ├── 用户列表(type=3)
 * ├── 新增用户(type=3)
 * ├── 修改用户(type=3)
 * ├── 删除用户(type=3)
 * └── 物理删除(type=3)
 * <p>
 * 因为：
 * <p>
 * 系统管理（type=1）
 * └── 用户管理（type=2）
 * <p>
 * 所以左侧菜单就是：
 * <p>
 * ┌─────────────────────────────┐
 * │ 首页                        │
 * │                             │
 * │ 系统管理                    │
 * │   └── 用户管理              │
 * │                             │
 * └─────────────────────────────┘
 * <p>
 * 数据库操作
 * <p>
 * INSERT INTO sys_permission
 * (
 * id,
 * parent_id,
 * name,
 * menu_path,
 * component,
 * permission_key,
 * icon,
 * type,
 * sort,
 * visible,
 * status,
 * redirect,
 * is_cache,
 * is_frame,
 * remark,
 * delete_flag
 * )
 * VALUES
 * (
 * 1,
 * 0,
 * '系统管理',
 * '/system',
 * 'Layout',
 * '',
 * 'Setting',
 * 1,
 * 1,
 * 1,
 * 1,
 * '/system/user',
 * 1,
 * 0,
 * '系统管理目录',
 * 0
 * ),
 * (
 * 2,
 * 1,
 * '用户管理',
 * '/system/user',
 * 'system/user/index',
 * '',
 * 'User',
 * 2,
 * 1,
 * 1,
 * 1,
 * '',
 * 1,
 * 0,
 * '用户管理菜单',
 * 0
 * ),
 * (
 * 3,
 * 2,
 * '用户列表',
 * '',
 * '',
 * 'sys:user:list',
 * '',
 * 3,
 * 1,
 * 1,
 * 1,
 * '',
 * 1,
 * 0,
 * '查询用户权限',
 * 0
 * ),
 * (
 * 4,
 * 2,
 * '新增用户',
 * '',
 * '',
 * 'sys:user:add',
 * '',
 * 3,
 * 2,
 * 1,
 * 1,
 * '',
 * 1,
 * 0,
 * '新增用户权限',
 * 0
 * ),
 * (
 * 5,
 * 2,
 * '修改用户',
 * '',
 * '',
 * 'sys:user:edit',
 * '',
 * 3,
 * 3,
 * 1,
 * 1,
 * '',
 * 1,
 * 0,
 * '修改用户权限',
 * 0
 * ),
 * (
 * 6,
 * 2,
 * '删除用户',
 * '',
 * '',
 * 'sys:user:remove',
 * '',
 * 3,
 * 4,
 * 1,
 * 1,
 * '',
 * 1,
 * 0,
 * '逻辑删除用户权限',
 * 0
 * ),
 * (
 * 7,
 * 2,
 * '物理删除',
 * '',
 * '',
 * 'sys:user:physicalDel',
 * '',
 * 3,
 * 5,
 * 1,
 * 1,
 * '',
 * 1,
 * 0,
 * '管理员专用物理删除权限',
 * 0
 * );
 */

@Data
@TableName("sys_permission")
public class SysPermission {

    @TableId(type = IdType.ASSIGN_ID)// 改为雪花算法分布式 ID
    private Long id;

    /**
     * 父权限ID
     */
    private Long parentId;

    /**
     * 菜单/权限名称
     */
    private String name;

    /**
     * 权限标识（核心：sys:user:list、sys:user:add）
     */
    private String permissionKey;

    /**
     * 类型 1菜单 2按钮权限
     */
    private Integer type;

    /**
     * 逻辑删除
     */
    @TableLogic
    private Integer deleteFlag;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}