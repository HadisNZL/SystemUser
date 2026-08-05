package com.system.gateway.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.Exceptions;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * 网关路由异常统一响应。
 */
@Component
@Order(-2)
public class GatewayExceptionHandler implements ErrorWebExceptionHandler {

    private final ObjectMapper objectMapper;

    public GatewayExceptionHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable throwable) {
        if (exchange.getResponse().isCommitted()) {
            return Mono.error(throwable);
        }

        HttpStatusCode status = resolveStatus(throwable);
        String message = status.value() == HttpStatus.SERVICE_UNAVAILABLE.value()
                ? "服务暂时不可用，请稍后再试"
                : "网关处理请求失败";
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("code", status.value());
        body.put("msg", message);
        body.put("isSuccess", false);
        body.put("data", null);

        byte[] bytes;
        try {
            bytes = objectMapper.writeValueAsBytes(body);
        } catch (JsonProcessingException ignored) {
            bytes = "{\"code\":500,\"msg\":\"网关处理请求失败\",\"isSuccess\":false,\"data\":null}"
                    .getBytes(StandardCharsets.UTF_8);
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(bytes)));
    }

    private HttpStatusCode resolveStatus(Throwable throwable) {
        Throwable cause = Exceptions.unwrap(throwable);
        while (cause != null) {
            if (cause instanceof ResponseStatusException responseStatusException) {
                return responseStatusException.getStatusCode();
            }
            if (cause instanceof IOException || cause instanceof TimeoutException) {
                return HttpStatus.SERVICE_UNAVAILABLE;
            }
            cause = cause.getCause();
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
