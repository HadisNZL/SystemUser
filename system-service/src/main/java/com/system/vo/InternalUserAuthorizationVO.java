package com.system.vo;

import lombok.Data;

import java.util.List;

/**
 * 内部服务恢复用户权限所需的信息。
 */
@Data
public class InternalUserAuthorizationVO {

    private Long userId;
    private Integer status;
    private List<String> permissionKeys;
}
