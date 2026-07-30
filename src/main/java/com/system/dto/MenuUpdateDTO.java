package com.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "修改菜单权限入参")
public class MenuUpdateDTO {

    @NotNull(message = "菜单ID不能为空")
    @Min(value = 1, message = "菜单ID必须大于等于1")
    @Schema(description = "菜单ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    @Min(value = 0, message = "父菜单ID必须大于等于0")
    @Schema(description = "父菜单ID，顶级目录传0")
    private Long parentId;

    @Size(max = 50, message = "菜单名称长度不能超过50个字符")
    @Schema(description = "菜单名称")
    private String name;

    @Size(max = 255, message = "路由地址长度不能超过255个字符")
    @Schema(description = "路由地址")
    private String menuPath;

    @Size(max = 255, message = "权限标识长度不能超过255个字符")
    @Schema(description = "权限标识")
    private String permissionKey;

    @Min(value = 1, message = "类型只能是1、2、3")
    @Max(value = 3, message = "类型只能是1、2、3")
    @Schema(description = "类型：1目录，2菜单，3按钮")
    private Integer type;

    @Min(value = 0, message = "排序必须大于等于0")
    @Schema(description = "排序")
    private Integer sort;

    @Size(max = 100, message = "图标长度不能超过100个字符")
    @Schema(description = "菜单图标")
    private String icon;

    @Size(max = 255, message = "组件路径长度不能超过255个字符")
    @Schema(description = "前端组件路径")
    private String component;

    @Min(value = 0, message = "是否显示只能是0或1")
    @Max(value = 1, message = "是否显示只能是0或1")
    @Schema(description = "是否显示：1显示，0隐藏")
    private Integer visible;

    @Min(value = 0, message = "状态只能是0或1")
    @Max(value = 1, message = "状态只能是0或1")
    @Schema(description = "状态：1启用，0禁用")
    private Integer status;

    @Size(max = 255, message = "重定向地址长度不能超过255个字符")
    @Schema(description = "路由重定向")
    private String redirect;

    @Min(value = 0, message = "是否缓存只能是0或1")
    @Max(value = 1, message = "是否缓存只能是0或1")
    @Schema(description = "是否缓存：1缓存，0不缓存")
    private Integer isCache;

    @Min(value = 0, message = "是否外链只能是0或1")
    @Max(value = 1, message = "是否外链只能是0或1")
    @Schema(description = "是否外链：0否，1是")
    private Integer isFrame;

    @Size(max = 500, message = "备注长度不能超过500个字符")
    @Schema(description = "备注")
    private String remark;
}
