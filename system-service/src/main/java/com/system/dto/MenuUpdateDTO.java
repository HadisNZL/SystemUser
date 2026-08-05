package com.system.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 修改菜单权限入参。
 */
@Data
public class MenuUpdateDTO {

    @NotNull(message = "菜单ID不能为空")
    @Min(value = 1, message = "菜单ID必须大于等于1")
    private Long id;

    @Min(value = 0, message = "父菜单ID必须大于等于0")
    private Long parentId;

    @Size(max = 50, message = "菜单名称长度不能超过50个字符")
    private String name;

    @Size(max = 255, message = "路由地址长度不能超过255个字符")
    private String menuPath;

    @Size(max = 255, message = "权限标识长度不能超过255个字符")
    private String permissionKey;

    @Min(value = 1, message = "类型只能是1、2、3")
    @Max(value = 3, message = "类型只能是1、2、3")
    private Integer type;

    @Min(value = 0, message = "排序必须大于等于0")
    private Integer sort;

    @Size(max = 100, message = "图标长度不能超过100个字符")
    private String icon;

    @Size(max = 255, message = "组件路径长度不能超过255个字符")
    private String component;

    @Min(value = 0, message = "是否显示只能是0或1")
    @Max(value = 1, message = "是否显示只能是0或1")
    private Integer visible;

    @Min(value = 0, message = "状态只能是0或1")
    @Max(value = 1, message = "状态只能是0或1")
    private Integer status;

    @Size(max = 255, message = "重定向地址长度不能超过255个字符")
    private String redirect;

    @Min(value = 0, message = "是否缓存只能是0或1")
    @Max(value = 1, message = "是否缓存只能是0或1")
    private Integer isCache;

    @Min(value = 0, message = "是否外链只能是0或1")
    @Max(value = 1, message = "是否外链只能是0或1")
    private Integer isFrame;

    @Size(max = 500, message = "备注长度不能超过500个字符")
    private String remark;
}
