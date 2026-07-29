package com.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "修改用户状态入参")
public class UserStatusDTO {

    @NotNull(message = "用户ID不能为空")
    @Min(value = 1, message = "用户ID必须大于等于1")
    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    @NotNull(message = "状态不能为空")
    @Min(value = 0, message = "状态只能是0或1")
    @Max(value = 1, message = "状态只能是0或1")
    @Schema(description = "状态：0禁用，1启用", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer status;
}
