package com.system.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class UserAssignRoleDTO {

    @NotNull(message = "角色ID列表不能为空")
    private List<@NotNull(message = "角色ID不能为空") @Min(value = 1, message = "角色ID必须大于等于1") Long> roleIds;
}
