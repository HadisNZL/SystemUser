package com.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "修改用户入参")
public class UserUpdateDTO {

    @NotNull(message = "用户ID不能为空")
    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;// 类型保持 Long（雪花 ID 是长整数，不能用 Integer）

    @Size(max = 50, message = "昵称长度不能超过50个字符")
    @Schema(description = "用户昵称")
    private String nickname;

    @Pattern(regexp = "^$|^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @Schema(description = "手机号")
    private String phone;

    @Email(message = "邮箱格式不正确")
    @Size(max = 50, message = "邮箱长度不能超过50个字符")
    @Schema(description = "邮箱")
    private String email;

    @Min(value = 0, message = "状态只能是0或1")
    @Max(value = 1, message = "状态只能是0或1")
    @Schema(description = "状态：0禁用，1启用")
    private Integer status;
}
