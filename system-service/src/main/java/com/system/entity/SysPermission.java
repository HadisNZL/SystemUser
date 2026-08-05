package com.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 菜单权限表实体，目录、菜单、按钮权限统一存储。
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
     * 类型 1目录 2菜单 3按钮权限
     */
    private Integer type;

    /**
     * 路由地址
     */
    private String menuPath;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 菜单图标
     */
    private String icon;

    /**
     * 前端组件路径
     */
    private String component;

    /**
     * 是否显示 1显示 0隐藏
     */
    private Integer visible;

    /**
     * 状态 1启用 0禁用
     */
    private Integer status;

    /**
     * 路由重定向
     */
    private String redirect;

    /**
     * 是否缓存 1缓存 0不缓存
     */
    private Integer isCache;

    /**
     * 是否外链 0否 1是
     */
    private Integer isFrame;

    /**
     * 备注
     */
    private String remark;

    /**
     * 逻辑删除
     */
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Integer deleteFlag;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
