package com.system.config;

import com.system.filter.GatewayForwardAuthenticationFilter;
import com.system.security.handler.CustomAccessDeniedHandler;
import com.system.security.handler.CustomAuthEntryPoint;
import jakarta.annotation.Resource;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * 系统服务安全配置，只信任网关透传的登录上下文。
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Resource
    private GatewayForwardAuthenticationFilter gatewayForwardAuthenticationFilter;
    @Resource
    private CustomAuthEntryPoint customAuthEntryPoint;
    @Resource
    private CustomAccessDeniedHandler customAccessDeniedHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public FilterRegistrationBean<GatewayForwardAuthenticationFilter> gatewayForwardFilterRegistration() {
        FilterRegistrationBean<GatewayForwardAuthenticationFilter> registration =
                new FilterRegistrationBean<>(gatewayForwardAuthenticationFilter);
        registration.setEnabled(false);
        return registration;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(customAuthEntryPoint)
                        .accessDeniedHandler(customAccessDeniedHandler))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/internal/auth/**", "/internal/authorization/**", "/system/demo/**", "/actuator/health", "/doc.html", "/v3/api-docs/**", "/webjars/**").permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(gatewayForwardAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
