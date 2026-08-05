package com.system.controller;

import com.system.common.PageResult;
import com.system.common.Result;
import com.system.common.SystemConstants;
import com.system.dto.OperationLogSearchDTO;
import com.system.service.OperationLogQueryService;
import com.system.vo.OperationLogVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 操作日志查询接口。
 */
@Validated
@RestController
@RequestMapping("/log/operation")
@Tag(name = "操作日志模块", description = "提供系统操作日志的条件分页查询接口")
public class OperationLogController {

    private final OperationLogQueryService operationLogQueryService;

    public OperationLogController(OperationLogQueryService operationLogQueryService) {
        this.operationLogQueryService = operationLogQueryService;
    }

    @Operation(summary = "分页查询操作日志", description = "根据操作人、模块、状态和时间范围分页查询操作日志")
    @GetMapping("/search_list")
    @PreAuthorize("hasAuthority('sys:log:list')")
    public Result<PageResult<OperationLogVO>> getOperationLogList(
            @Valid OperationLogSearchDTO dto,
            @RequestParam(defaultValue = SystemConstants.DEFAULT_PAGE_NUM)
            @Min(value = 1, message = "页码必须大于等于1") Integer pageNum,
            @RequestParam(defaultValue = SystemConstants.DEFAULT_PAGE_SIZE)
            @Min(value = 1, message = "每页条数必须大于等于1")
            @Max(value = 100, message = "每页条数不能超过100") Integer pageSize) {
        return Result.success(operationLogQueryService.getOperationLogPage(dto, pageNum, pageSize));
    }
}
