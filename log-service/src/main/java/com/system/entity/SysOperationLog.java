package com.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 操作日志表实体。
 */
@Data
@TableName("sys_operation_log")
public class SysOperationLog {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private String module;
    private String operation;
    private String requestMethod;
    private String requestUri;
    private String requestParams;
    private String responseResult;
    private Integer status;
    private String errorMsg;
    private Long operatorId;
    private String ip;
    private Long costTime;
    private LocalDateTime createTime;
}
