package com.system.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * 操作日志分页查询条件。
 */
@Data
public class OperationLogSearchDTO {

    @Size(max = 100, message = "模块名称长度不能超过100个字符")
    private String module;

    @Size(max = 100, message = "操作名称长度不能超过100个字符")
    private String operation;

    @Min(value = 1, message = "操作人ID必须大于等于1")
    private Long operatorId;

    @Min(value = 0, message = "状态只能是0或1")
    @Max(value = 1, message = "状态只能是0或1")
    private Integer status;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
}
