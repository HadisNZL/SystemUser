package com.system.vo;

import lombok.Data;

/**
 * 认证服务登录校验所需的用户信息。
 */
@Data
public class InternalLoginUserVO {

    private Long id;
    private String username;
    private String password;
    private Integer status;
}
