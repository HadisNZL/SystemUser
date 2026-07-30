package com.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "角色分页视图对象")
public class RolePageVO {

    @Schema(description = "角色ID")
    private Long id;

    @Schema(description = "角色名称")
    private String roleName;

    @Schema(description = "角色标识")
    private String roleCode;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "状态：0禁用，1启用")
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
