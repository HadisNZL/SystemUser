package com.system.service;

import com.system.vo.FileUploadVO;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface SysFileService {

    FileUploadVO uploadFile(MultipartFile file);

    Resource loadFileResource(Long id);

    String getOriginalFilename(Long id);
}
