package com.system.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * sysUser出参实体类
 */
@Data
public class UserPageVO {
    private Long id;
    private String username;
    private String nickname;
    private String phone;
    private String email;
    private Integer status;
    private LocalDateTime createTime;
}
