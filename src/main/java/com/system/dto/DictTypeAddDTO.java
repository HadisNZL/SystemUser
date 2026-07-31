package com.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 新增字典类型入参。
 */
@Data
@Schema(description = "新增字典类型入参")
public class DictTypeAddDTO {

    @NotBlank(message = "字典名称不能为空")
    @Size(max = 50, message = "字典名称长度不能超过50个字符")
    @Schema(description = "字典名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String dictName;

    @NotBlank(message = "字典类型不能为空")
    @Size(max = 100, message = "字典类型长度不能超过100个字符")
    @Pattern(regexp = "^[a-z][a-z0-9_]*$", message = "字典类型只能包含小写字母、数字和下划线，且以字母开头")
    @Schema(description = "字典类型", requiredMode = Schema.RequiredMode.REQUIRED)
    private String dictType;

    @Min(value = 0, message = "状态只能是0或1")
    @Max(value = 1, message = "状态只能是0或1")
    @Schema(description = "状态：0禁用，1启用")
    private Integer status;

    @Size(max = 200, message = "备注长度不能超过200个字符")
    @Schema(description = "备注")
    private String remark;
}
