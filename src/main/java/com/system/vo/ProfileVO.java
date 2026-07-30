package com.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "当前登录用户信息视图对象")
public class ProfileVO {

    @Schema(description = "当前用户基础信息")
    private UserDetailVO user;

    @Schema(description = "当前用户拥有的角色")
    private List<RolePageVO> roles;

    @Schema(description = "当前用户拥有的权限标识")
    private List<String> permissions;
}
