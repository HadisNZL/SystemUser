package com.system.service.impl;

import com.system.common.BusinessException;
import com.system.entity.SysFile;
import com.system.mapper.SysFileMapper;
import com.system.vo.FileUploadVO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * 文件存储服务测试。
 */
class SysFileServiceImplTest {

    @TempDir
    Path tempDir;

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void uploadShouldStoreFileAndMetadata() throws Exception {
        SysFileMapper mapper = mock(SysFileMapper.class);
        when(mapper.insert(any(SysFile.class))).thenAnswer(invocation -> {
            SysFile entity = invocation.getArgument(0);
            entity.setId(100L);
            return 1;
        });
        SysFileServiceImpl service = new SysFileServiceImpl(mapper);
        ReflectionTestUtils.setField(service, "uploadPath", tempDir.toString());
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("1", null));
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.txt", "text/plain", "hello".getBytes());

        FileUploadVO result = service.uploadFile(file);

        assertEquals(100L, result.getId());
        assertEquals("/file/download/100", result.getDownloadUrl());
        try (Stream<Path> paths = Files.walk(tempDir)) {
            assertTrue(paths.anyMatch(Files::isRegularFile));
        }
    }

    @Test
    void uploadShouldRejectUnsupportedExtension() {
        SysFileServiceImpl service = new SysFileServiceImpl(mock(SysFileMapper.class));
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.exe", "application/octet-stream", "data".getBytes());

        BusinessException exception = assertThrows(BusinessException.class, () -> service.uploadFile(file));

        assertEquals("文件类型不支持", exception.getMessage());
    }
}
