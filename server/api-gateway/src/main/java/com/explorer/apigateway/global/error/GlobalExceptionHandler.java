package com.explorer.apigateway.global.error;

import com.explorer.apigateway.global.common.dto.Message;
import com.explorer.apigateway.global.error.exception.AuthenticationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Order(-2)
@Component
@RequiredArgsConstructor
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {
    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        Message errorResponse = null;
        DataBuffer dataBuffer = null;

        DataBufferFactory bufferFactory = exchange.getResponse().bufferFactory();

        exchange.getResponse().getHeaders().setContentType(APPLICATION_JSON);

        if (ex instanceof AuthenticationException) {
//            errorResponse = ErrorResponse.create(ex, HttpStatus.UNAUTHORIZED, ex.getMessage());
            errorResponse = Message.fail(String.valueOf(((AuthenticationException) ex).getErrorCode()), ex.getMessage());
        } else {
//            errorResponse = ErrorResponse.create(ex, HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
            errorResponse = Message.fail("INTERNAL_SERVER_ERROR", "");
        }

        try {
            dataBuffer =
                    bufferFactory.wrap(objectMapper.writeValueAsBytes(errorResponse));
        } catch (JsonProcessingException e) {
            bufferFactory.wrap("".getBytes());
        }

        exchange.getResponse().setStatusCode(HttpStatus.OK);

        return exchange.getResponse().writeWith(Mono.just(dataBuffer));
    }
}