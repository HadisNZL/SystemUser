package com.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 系统展示信息响应对象。
 */
@Data
@Schema(description = "系统展示信息")
public class SystemInfoVO {

    @Schema(description = "系统名称")
    private String name;

    @Schema(description = "系统描述")
    private String description;

    @Schema(description = "系统版本")
    private String version;
}
