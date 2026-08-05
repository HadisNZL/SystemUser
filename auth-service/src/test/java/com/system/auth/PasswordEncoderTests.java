package com.system.auth;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * BCrypt密码生成测试。
 */
class PasswordEncoderTests {

    @Test
    void generatePassword() {
        String rawPassword = System.getProperty("rawPassword", "123456");
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(rawPassword);
        System.out.println("BCrypt password: " + encodedPassword);
        assertTrue(passwordEncoder.matches(rawPassword, encodedPassword));
    }
}
