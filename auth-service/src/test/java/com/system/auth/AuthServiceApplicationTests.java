package com.system.auth;

import com.system.auth.client.SystemServiceAuthClient;
import com.system.auth.vo.LoginUserVO;
import com.system.common.Result;
import com.system.common.ResultCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(properties = "spring.profiles.active=test")
class AuthServiceApplicationTests {

    @Autowired
    private SystemServiceAuthClient systemServiceAuthClient;

    @Test
    void contextLoads() {
    }

    @Test
    void feignShouldFallbackWhenSystemServiceUnavailable() {
        Result<LoginUserVO> result = systemServiceAuthClient.getLoginUser(
                "admin", SystemServiceAuthClient.INTERNAL_SOURCE_VALUE);

        assertEquals(ResultCode.SERVICE_UNAVAILABLE.getCode(), result.getCode());
    }
}
