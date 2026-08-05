package com.system.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 修改字典类型入参。
 */
@Data
public class DictTypeUpdateDTO {

    @NotNull(message = "字典类型ID不能为空")
    @Min(value = 1, message = "字典类型ID必须大于等于1")
    private Long id;

    @Size(max = 50, message = "字典名称长度不能超过50个字符")
    private String dictName;

    @Size(max = 100, message = "字典类型长度不能超过100个字符")
    @Pattern(regexp = "^$|^[a-z][a-z0-9_]*$", message = "字典类型只能包含小写字母、数字和下划线，且以字母开头")
    private String dictType;

    @Min(value = 0, message = "状态只能是0或1")
    @Max(value = 1, message = "状态只能是0或1")
    private Integer status;

    @Size(max = 200, message = "备注长度不能超过200个字符")
    private String remark;
}
