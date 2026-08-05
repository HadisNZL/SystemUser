package com.system.controller;

import com.system.annotation.OperationLog;
import com.system.common.Result;
import com.system.service.SysFileService;
import com.system.vo.FileUploadVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;

/**
 * 文件上传下载接口。
 */
@Validated
@RestController
@RequestMapping("/file")
@Tag(name = "文件管理模块", description = "提供文件上传、下载和文件元数据管理接口")
public class SysFileController {

    private final SysFileService sysFileService;

    public SysFileController(SysFileService sysFileService) {
        this.sysFileService = sysFileService;
    }

    @Operation(summary = "上传文件", description = "上传文件并保存文件元数据，返回文件ID和下载地址")
    @PostMapping("/upload")
    @OperationLog(module = "文件管理", operation = "上传文件")
    @PreAuthorize("isAuthenticated()")
    public Result<FileUploadVO> uploadFile(@RequestParam("file") MultipartFile file) {
        return Result.success(sysFileService.uploadFile(file));
    }

    @Operation(summary = "下载文件", description = "通过文件ID读取文件并以附件形式下载")
    @GetMapping("/download/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<org.springframework.core.io.Resource> downloadFile(
            @PathVariable @Min(value = 1, message = "文件ID必须大于等于1") Long id) {
        org.springframework.core.io.Resource resource = sysFileService.loadFileResource(id);
        String filename = sysFileService.getOriginalFilename(id);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
                        .filename(filename, StandardCharsets.UTF_8)
                        .build()
                        .toString())
                .body(resource);
    }
}
