package com.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 字典类型查询入参。
 */
@Data
@Schema(description = "字典类型查询入参")
public class DictTypeSearchDTO {

    @Size(max = 50, message = "字典名称长度不能超过50个字符")
    @Schema(description = "字典名称")
    private String dictName;

    @Size(max = 100, message = "字典类型长度不能超过100个字符")
    @Schema(description = "字典类型")
    private String dictType;

    @Min(value = 0, message = "状态只能是0或1")
    @Max(value = 1, message = "状态只能是0或1")
    @Schema(description = "状态：0禁用，1启用")
    private Integer status;
}
