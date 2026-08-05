package com.system.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 字典数据返回对象。
 */
@Data
public class DictDataVO {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    private String dictType;

    private String dictLabel;

    private String dictValue;

    private Integer sort;

    private Integer status;

    private String remark;

    private LocalDateTime createTime;
}
