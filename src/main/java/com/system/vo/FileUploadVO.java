package com.system.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "文件上传返回对象")
public class FileUploadVO {

    @Schema(description = "文件ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @Schema(description = "原始文件名")
    private String originalName;

    @Schema(description = "文件大小，单位字节")
    private Long fileSize;

    @Schema(description = "文件类型")
    private String contentType;

    @Schema(description = "下载地址")
    private String downloadUrl;
}
