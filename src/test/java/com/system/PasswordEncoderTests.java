package com.system;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 明文转密文
 */
class PasswordEncoderTests {

    @Test
    void encodeRawPassword() {
        String rawPassword = System.getProperty("rawPassword", "123456");
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        String encodedPassword = encoder.encode(rawPassword);

        System.out.println("rawPassword = " + rawPassword);
        System.out.println("encodedPassword = " + encodedPassword);


        assertThat(encoder.matches(rawPassword, encodedPassword)).isTrue();
    }
}
