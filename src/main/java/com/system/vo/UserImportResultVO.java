package com.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户导入结果。
 */
@Data
@Schema(description = "用户导入结果")
public class UserImportResultVO {

    @Schema(description = "成功数量")
    private Integer successCount = 0;

    @Schema(description = "失败数量")
    private Integer failureCount = 0;

    @Schema(description = "失败明细")
    private List<UserImportFailureVO> failures = new ArrayList<>();
}
