package com.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "角色查询入参")
public class RoleSearchDTO {

    @Size(max = 50, message = "角色名称长度不能超过50个字符")
    @Schema(description = "角色名称模糊查询")
    private String roleName;

    @Size(max = 50, message = "角色标识长度不能超过50个字符")
    @Schema(description = "角色标识模糊查询")
    private String roleCode;
}
