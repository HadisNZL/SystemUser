package com.system.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 修改角色入参。
 */
@Data
public class RoleUpdateDTO {

    @NotNull(message = "角色ID不能为空")
    @Min(value = 1, message = "角色ID必须大于等于1")
    private Long id;

    @Size(max = 50, message = "角色名称长度不能超过50个字符")
    private String roleName;

    @Size(max = 50, message = "角色标识长度不能超过50个字符")
    private String roleCode;

    @Min(value = 0, message = "排序必须大于等于0")
    private Integer sort;

    @Min(value = 0, message = "状态只能是0或1")
    @Max(value = 1, message = "状态只能是0或1")
    private Integer status;
}
