package com.system.controller;

import com.system.common.GlobalExceptionHandler;
import com.system.service.SysFileService;
import com.system.vo.FileUploadVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.multipart.MultipartFile;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SysFileControllerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        SysFileController sysFileController = new SysFileController();
        ReflectionTestUtils.setField(sysFileController, "sysFileService", new TestSysFileService());
        mockMvc = MockMvcBuilders.standaloneSetup(sysFileController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator())
                .build();
    }

    @Test
    void uploadFileShouldReturnFileInfo() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "hello".getBytes());

        mockMvc.perform(multipart("/sys/file/upload").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.originalName").value("test.txt"))
                .andExpect(jsonPath("$.data.downloadUrl").value("/sys/file/download/1"));
    }

    @Test
    void downloadFileShouldReturnResource() throws Exception {
        mockMvc.perform(get("/sys/file/download/1"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", containsString("test.txt")))
                .andExpect(content().bytes("hello".getBytes()));
    }

    private Validator validator() {
        LocalValidatorFactoryBean validatorFactoryBean = new LocalValidatorFactoryBean();
        validatorFactoryBean.afterPropertiesSet();
        return validatorFactoryBean;
    }

    private static class TestSysFileService implements SysFileService {

        @Override
        public FileUploadVO uploadFile(MultipartFile file) {
            FileUploadVO vo = new FileUploadVO();
            vo.setId(1L);
            vo.setOriginalName(file.getOriginalFilename());
            vo.setContentType(file.getContentType());
            vo.setFileSize(file.getSize());
            vo.setDownloadUrl("/sys/file/download/1");
            return vo;
        }

        @Override
        public Resource loadFileResource(Long id) {
            return new ByteArrayResource("hello".getBytes());
        }

        @Override
        public String getOriginalFilename(Long id) {
            return "test.txt";
        }
    }
}
