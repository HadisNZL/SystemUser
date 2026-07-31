package com.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 新增字典数据入参。
 */
@Data
@Schema(description = "新增字典数据入参")
public class DictDataAddDTO {

    @NotBlank(message = "字典类型不能为空")
    @Size(max = 100, message = "字典类型长度不能超过100个字符")
    @Schema(description = "字典类型", requiredMode = Schema.RequiredMode.REQUIRED)
    private String dictType;

    @NotBlank(message = "字典标签不能为空")
    @Size(max = 50, message = "字典标签长度不能超过50个字符")
    @Schema(description = "字典标签", requiredMode = Schema.RequiredMode.REQUIRED)
    private String dictLabel;

    @NotBlank(message = "字典值不能为空")
    @Size(max = 50, message = "字典值长度不能超过50个字符")
    @Schema(description = "字典值", requiredMode = Schema.RequiredMode.REQUIRED)
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
