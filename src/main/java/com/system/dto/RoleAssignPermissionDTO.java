package com.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "角色分配权限入参")
public class RoleAssignPermissionDTO {

    @NotNull(message = "权限ID列表不能为空")
    @Schema(description = "权限ID列表，传空数组表示清空权限", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<@NotNull(message = "权限ID不能为空") @Min(value = 1, message = "权限ID必须大于等于1") Long> permissionIds;
}
