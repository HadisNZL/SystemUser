package com.system.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 角色分页查询入参。
 */
@Data
public class RoleSearchDTO {

    @Size(max = 50, message = "角色名称长度不能超过50个字符")
    private String roleName;

    @Size(max = 50, message = "角色标识长度不能超过50个字符")
    private String roleCode;
}
