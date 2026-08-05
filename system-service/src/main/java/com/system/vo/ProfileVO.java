package com.system.vo;

import lombok.Data;

import java.util.List;

/**
 * 当前登录用户信息视图。
 */
@Data
public class ProfileVO {

    private UserDetailVO user;

    private List<RolePageVO> roles;

    private List<String> permissions;
}
