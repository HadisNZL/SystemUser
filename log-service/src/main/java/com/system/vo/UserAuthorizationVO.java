package com.system.vo;

import lombok.Data;

import java.util.List;

/**
 * system-service 返回的用户授权信息。
 */
@Data
public class UserAuthorizationVO {

    private Long userId;
    private Integer status;
    private List<String> permissionKeys;
}
