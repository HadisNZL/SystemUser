package com.system.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserStatusDTO {

    @NotNull(message = "用户ID不能为空")
    @Min(value = 1, message = "用户ID必须大于等于1")
    private Long id;

    @NotNull(message = "状态不能为空")
    @Min(value = 0, message = "状态只能是0或1")
    @Max(value = 1, message = "状态只能是0或1")
    private Integer status;
}
