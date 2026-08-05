package com.system.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 字典类型查询入参。
 */
@Data
public class DictTypeSearchDTO {

    @Size(max = 50, message = "字典名称长度不能超过50个字符")
    private String dictName;

    @Size(max = 100, message = "字典类型长度不能超过100个字符")
    private String dictType;

    @Min(value = 0, message = "状态只能是0或1")
    @Max(value = 1, message = "状态只能是0或1")
    private Integer status;
}
