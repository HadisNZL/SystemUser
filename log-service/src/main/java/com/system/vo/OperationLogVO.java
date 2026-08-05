package com.system.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 操作日志分页数据。
 */
@Data
public class OperationLogVO {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    private String module;
    private String operation;
    private String requestMethod;
    private String requestUri;
    private Integer status;
    private String errorMsg;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long operatorId;
    private String ip;
    private Long costTime;
    private LocalDateTime createTime;
}
