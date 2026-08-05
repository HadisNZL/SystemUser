package com.system.config;

import com.system.filter.GatewayForwardAuthenticationFilter;
import com.system.security.handler.CustomAccessDeniedHandler;
import com.system.security.handler.CustomAuthEntryPoint;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * 文件服务安全配置。
 */
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final GatewayForwardAuthenticationFilter gatewayForwardAuthenticationFilter;
    private final CustomAuthEntryPoint customAuthEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    public SecurityConfig(GatewayForwardAuthenticationFilter gatewayForwardAuthenticationFilter,
                          CustomAuthEntryPoint customAuthEntryPoint,
                          CustomAccessDeniedHandler customAccessDeniedHandler) {
        this.gatewayForwardAuthenticationFilter = gatewayForwardAuthenticationFilter;
        this.customAuthEntryPoint = customAuthEntryPoint;
        this.customAccessDeniedHandler = customAccessDeniedHandler;
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
                        .requestMatchers("/actuator/health", "/v3/api-docs/**").permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(gatewayForwardAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
