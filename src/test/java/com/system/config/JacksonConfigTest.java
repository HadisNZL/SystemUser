package com.system.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.system.vo.FileUploadVO;
import org.junit.jupiter.api.Test;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import static org.junit.jupiter.api.Assertions.assertTrue;

class JacksonConfigTest {

    @Test
    void idShouldSerializeAsStringAndFileSizeShouldRemainNumber() throws Exception {
        JacksonConfig jacksonConfig = new JacksonConfig();
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        jacksonConfig.jackson2ObjectMapperBuilderCustomizer().customize(builder);
        ObjectMapper objectMapper = builder.build();

        FileUploadVO vo = new FileUploadVO();
        vo.setId(2083000175466446849L);
        vo.setFileSize(101438L);

        String json = objectMapper.writeValueAsString(vo);

        assertTrue(json.contains("\"id\":\"2083000175466446849\""));
        assertTrue(json.contains("\"fileSize\":101438"));
    }
}
