package com.system.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 角色分配权限入参。
 */
@Data
public class RoleAssignPermissionDTO {

    @NotNull(message = "权限ID列表不能为空")
    private List<@NotNull(message = "权限ID不能为空") @Min(value = 1, message = "权限ID必须大于等于1") Long> permissionIds;
}
