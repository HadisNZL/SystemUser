package com.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 修改字典数据入参。
 */
@Data
@Schema(description = "修改字典数据入参")
public class DictDataUpdateDTO {

    @NotNull(message = "字典数据ID不能为空")
    @Min(value = 1, message = "字典数据ID必须大于等于1")
    @Schema(description = "字典数据ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    @Size(max = 100, message = "字典类型长度不能超过100个字符")
    @Schema(description = "字典类型")
    private String dictType;

    @Size(max = 50, message = "字典标签长度不能超过50个字符")
    @Schema(description = "字典标签")
    private String dictLabel;

    @Size(max = 50, message = "字典值长度不能超过50个字符")
    @Schema(description = "字典值")
    private String dictValue;

    @Min(value = 0, message = "排序必须大于等于0")
    @Schema(description = "排序")
    private Integer sort;

    @Min(value = 0, message = "状态只能是0或1")
    @Max(value = 1, message = "状态只能是0或1")
    @Schema(description = "状态：0禁用，1启用")
    private Integer status;

    @Size(max = 200, message = "备注长度不能超过200个字符")
    @Schema(description = "备注")
    private String remark;
}
