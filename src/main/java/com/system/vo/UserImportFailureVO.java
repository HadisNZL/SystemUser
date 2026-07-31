package com.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 用户导入失败行信息。
 */
@Data
@Schema(description = "用户导入失败行信息")
public class UserImportFailureVO {

    @Schema(description = "Excel行号")
    private Integer rowNum;

    @Schema(description = "账号")
    private String username;

    @Schema(description = "失败原因")
    private String reason;
}
