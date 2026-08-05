package com.system.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 用户查询入参。
 */
@Data
public class UserSearchDTO {

    @Size(max = 30, message = "账号长度不能超过30个字符")
    private String username;

    @Min(value = 0, message = "状态只能是0或1")
    @Max(value = 1, message = "状态只能是0或1")
    private Integer status;

    @Pattern(regexp = "^$|^\\d{4}-\\d{2}-\\d{2}( \\d{2}:\\d{2}:\\d{2})?$", message = "开始时间格式必须是yyyy-MM-dd或yyyy-MM-dd HH:mm:ss")
    private String startTime;

    @Pattern(regexp = "^$|^\\d{4}-\\d{2}-\\d{2}( \\d{2}:\\d{2}:\\d{2})?$", message = "结束时间格式必须是yyyy-MM-dd或yyyy-MM-dd HH:mm:ss")
    private String endTime;
}
