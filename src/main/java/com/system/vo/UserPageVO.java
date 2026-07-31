package com.system.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * sysUser出参实体类
 * schema是knife4j的注释
 */
@Data
@Schema(description = "用户分页视图对象")
public class UserPageVO {
    @Schema(description = "主键id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    @Schema(description = "登录账号")
    private String username;
    @Schema(description = "用户昵称")
    private String nickname;
    @Schema(description = "手机号")
    private String phone;
    @Schema(description = "邮箱")
    private String email;
    @Schema(description = "账号状态 1正常 0禁用")
    private Integer status;
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
