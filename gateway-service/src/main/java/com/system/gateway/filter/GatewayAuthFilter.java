package com.system.gateway.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.system.gateway.service.TokenBlacklistService;
import com.system.gateway.util.JwtUtil;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 网关统一鉴权过滤器。
 */
@Component
public class GatewayAuthFilter implements GlobalFilter, Ordered {

    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
    private static final List<String> WHITE_LIST = List.of(
            "/auth/captcha",
            "/auth/login",
            "/system/demo/**",
            "/actuator/health",
            "/doc.html",
            "/webjars/**",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/knife4j/**"
    );

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;
    private final TokenBlacklistService tokenBlacklistService;

    public GatewayAuthFilter(JwtUtil jwtUtil, ObjectMapper objectMapper, TokenBlacklistService tokenBlacklistService) {
        this.jwtUtil = jwtUtil;
        this.objectMapper = objectMapper;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        if (HttpMethod.OPTIONS.equals(request.getMethod()) || isWhitePath(request.getURI().getPath())) {
            return chain.filter(exchange);
        }

        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return unauthorized(exchange, "未登录，请先登录");
        }

        String token = authHeader.substring("Bearer ".length()).trim();
        if (!jwtUtil.validateToken(token)) {
            return unauthorized(exchange, "登录令牌无效，请重新登录");
        }

        return tokenBlacklistService.isBlacklisted(token)
                .flatMap(blacklisted -> {
                    if (Boolean.TRUE.equals(blacklisted)) {
                        return unauthorized(exchange, "登录已退出，请重新登录");
                    }
                    Long userId = jwtUtil.getUserId(token);
                    ServerHttpRequest mutatedRequest = request.mutate()
                            .header("X-Gateway-Forwarded", "true")
                            .header("X-User-Id", userId.toString())
                            .build();
                    return chain.filter(exchange.mutate().request(mutatedRequest).build());
                });
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 10;
    }

    private boolean isWhitePath(String path) {
        return WHITE_LIST.stream().anyMatch(pattern -> PATH_MATCHER.match(pattern, path));
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String msg) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("code", HttpStatus.UNAUTHORIZED.value());
        body.put("msg", msg);
        body.put("isSuccess", false);
        body.put("data", null);

        byte[] bytes;
        try {
            bytes = objectMapper.writeValueAsBytes(body);
        } catch (JsonProcessingException e) {
            bytes = "{\"code\":401,\"msg\":\"未登录，请先登录\",\"isSuccess\":false,\"data\":null}".getBytes(StandardCharsets.UTF_8);
        }

        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(bytes)));
    }
}
