package com.system.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 修改字典数据入参。
 */
@Data
public class DictDataUpdateDTO {

    @NotNull(message = "字典数据ID不能为空")
    @Min(value = 1, message = "字典数据ID必须大于等于1")
    private Long id;

    @Size(max = 100, message = "字典类型长度不能超过100个字符")
    private String dictType;

    @Size(max = 50, message = "字典标签长度不能超过50个字符")
    private String dictLabel;

    @Size(max = 50, message = "字典值长度不能超过50个字符")
    private String dictValue;

    @Min(value = 0, message = "排序必须大于等于0")
    private Integer sort;

    @Min(value = 0, message = "状态只能是0或1")
    @Max(value = 1, message = "状态只能是0或1")
    private Integer status;

    @Size(max = 200, message = "备注长度不能超过200个字符")
    private String remark;
}
