package com.system.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 字典类型返回对象。
 */
@Data
public class DictTypeVO {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    private String dictName;

    private String dictType;

    private Integer status;

    private String remark;

    private LocalDateTime createTime;
}
