package com.system.gateway.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;

import java.net.ConnectException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GatewayExceptionHandlerTest {

    private final GatewayExceptionHandler handler = new GatewayExceptionHandler(new ObjectMapper());

    @Test
    void shouldReturnServiceUnavailableWhenNoServiceInstanceExists() {
        MockServerWebExchange exchange = exchange();

        handler.handle(exchange, new NotFoundException("system-service unavailable")).block();

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, exchange.getResponse().getStatusCode());
        assertTrue(exchange.getResponse().getBodyAsString().block().contains("\"code\":503"));
    }

    @Test
    void shouldReturnServiceUnavailableWhenConnectionFails() {
        MockServerWebExchange exchange = exchange();

        handler.handle(exchange, new RuntimeException(new ConnectException("connection refused"))).block();

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, exchange.getResponse().getStatusCode());
    }

    private MockServerWebExchange exchange() {
        return MockServerWebExchange.from(MockServerHttpRequest.get("/system/user/1").build());
    }
}
