package com.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 字典数据查询入参。
 */
@Data
@Schema(description = "字典数据查询入参")
public class DictDataSearchDTO {

    @Size(max = 100, message = "字典类型长度不能超过100个字符")
    @Schema(description = "字典类型")
    private String dictType;

    @Size(max = 50, message = "字典标签长度不能超过50个字符")
    @Schema(description = "字典标签")
    private String dictLabel;

    @Min(value = 0, message = "状态只能是0或1")
    @Max(value = 1, message = "状态只能是0或1")
    @Schema(description = "状态：0禁用，1启用")
    private Integer status;
}
