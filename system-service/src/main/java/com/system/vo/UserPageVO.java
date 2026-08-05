package com.system.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户分页视图。
 */
@Data
public class UserPageVO {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    private String username;
    private String nickname;
    private String phone;
    private String email;
    private Integer status;
    private LocalDateTime createTime;
}
