package com.system.service.impl;

import com.system.common.BusinessException;
import com.system.common.ResultCode;
import com.system.entity.SysFile;
import com.system.mapper.SysFileMapper;
import com.system.service.SysFileService;
import com.system.util.SecurityUtil;
import com.system.vo.FileUploadVO;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
public class SysFileServiceImpl implements SysFileService {

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024L;
    private static final DateTimeFormatter DATE_PATH_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");
    private static final List<String> ALLOWED_EXTENSIONS = List.of("jpg", "jpeg", "png", "gif", "pdf", "txt", "doc", "docx", "xls", "xlsx");

    @Value("${system.file.upload-path:./upload}")
    private String uploadPath;

    @Resource
    private SysFileMapper sysFileMapper;

    @Override
    public FileUploadVO uploadFile(MultipartFile file) {
        validateFile(file);
        String originalName = file.getOriginalFilename();
        String extension = getExtension(originalName);
        String storageName = UUID.randomUUID() + "." + extension;
        String datePath = LocalDate.now().format(DATE_PATH_FORMATTER);
        Path targetDir = getUploadRoot().resolve(datePath);
        Path targetPath = targetDir.resolve(storageName).normalize();

        try {
            Files.createDirectories(targetDir);
            file.transferTo(targetPath);
        } catch (IOException e) {
            throw new BusinessException("文件上传失败");
        }

        SysFile sysFile = new SysFile();
        sysFile.setOriginalName(originalName);
        sysFile.setStorageName(storageName);
        sysFile.setRelativePath(datePath + "/" + storageName);
        sysFile.setContentType(file.getContentType());
        sysFile.setFileSize(file.getSize());
        sysFile.setUploaderId(SecurityUtil.getCurrentUserId());
        int rows = sysFileMapper.insert(sysFile);
        if (rows <= 0) {
            throw new BusinessException("文件记录保存失败");
        }
        return buildUploadVO(sysFile);
    }

    @Override
    public FileSystemResource loadFileResource(Long id) {
        SysFile sysFile = getFileById(id);
        Path filePath = resolveStoragePath(sysFile.getRelativePath());
        FileSystemResource resource = new FileSystemResource(filePath);
        if (!resource.exists() || !resource.isReadable()) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "文件不存在");
        }
        return resource;
    }

    @Override
    public String getOriginalFilename(Long id) {
        return getFileById(id).getOriginalName();
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("文件不能为空");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException("文件大小不能超过10MB");
        }
        String extension = getExtension(file.getOriginalFilename());
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new BusinessException("文件类型不支持");
        }
    }

    private String getExtension(String filename) {
        if (filename == null || filename.isBlank() || !filename.contains(".")) {
            throw new BusinessException("文件类型不支持");
        }
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
    }

    private SysFile getFileById(Long id) {
        SysFile sysFile = sysFileMapper.selectById(id);
        if (sysFile == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "文件记录不存在");
        }
        return sysFile;
    }

    private Path getUploadRoot() {
        return Path.of(uploadPath).toAbsolutePath().normalize();
    }

    private Path resolveStoragePath(String relativePath) {
        Path uploadRoot = getUploadRoot();
        Path filePath = uploadRoot.resolve(relativePath).normalize();
        if (!filePath.startsWith(uploadRoot)) {
            throw new BusinessException("文件路径不合法");
        }
        return filePath;
    }

    private FileUploadVO buildUploadVO(SysFile sysFile) {
        FileUploadVO vo = new FileUploadVO();
        vo.setId(sysFile.getId());
        vo.setOriginalName(sysFile.getOriginalName());
        vo.setFileSize(sysFile.getFileSize());
        vo.setContentType(sysFile.getContentType());
        vo.setDownloadUrl("/sys/file/download/" + sysFile.getId());
        return vo;
    }
}
