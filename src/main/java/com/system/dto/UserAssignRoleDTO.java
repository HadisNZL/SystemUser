package com.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "用户分配角色入参")
public class UserAssignRoleDTO {

    @NotNull(message = "角色ID列表不能为空")
    @Schema(description = "角色ID列表，传空数组表示清空角色", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<@NotNull(message = "角色ID不能为空") @Min(value = 1, message = "角色ID必须大于等于1") Long> roleIds;
}
