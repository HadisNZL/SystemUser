package com.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "用户查询入参")
public class UserSearchDTO {

    @Size(max = 30, message = "账号长度不能超过30个字符")
    @Schema(description = "账号模糊查询")
    private String username;

    @Min(value = 0, message = "状态只能是0或1")
    @Max(value = 1, message = "状态只能是0或1")
    @Schema(description = "状态精准查询")
    private Integer status;

    @Pattern(regexp = "^$|^\\d{4}-\\d{2}-\\d{2}( \\d{2}:\\d{2}:\\d{2})?$", message = "开始时间格式必须是yyyy-MM-dd或yyyy-MM-dd HH:mm:ss")
    @Schema(description = "开始时间")
    private String startTime;

    @Pattern(regexp = "^$|^\\d{4}-\\d{2}-\\d{2}( \\d{2}:\\d{2}:\\d{2})?$", message = "结束时间格式必须是yyyy-MM-dd或yyyy-MM-dd HH:mm:ss")
    @Schema(description = "结束时间")
    private String endTime;
}
