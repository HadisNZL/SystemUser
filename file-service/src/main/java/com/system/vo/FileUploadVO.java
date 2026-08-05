package com.system.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

/**
 * 文件上传结果。
 */
@Data
public class FileUploadVO {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    private String originalName;

    private Long fileSize;

    private String contentType;

    private String downloadUrl;
}
