package com.snazzybytes.reactiverestapi.functional;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Reactive Rest API using the FUNCTIONAL approach w/ custom exception handler.
 * Notice that sort @Order(-2) is needed to trigger OUR custom exception handler
 * instead of Spring's
 * {@link DefaultErrorWebExceptionHandler#getRoutingFunction()}
 * By default, spring's default handler's order is "-1"
 * {@link ErrorWebFluxAutoConfiguration#errorWebExceptionHandler()}.
 */
@Configuration(proxyBeanMethods = false)
@EnableWebFlux
public class PriceRequestRouter {

    @Bean
    public RouterFunction<ServerResponse> route(PriceRequestHandler requestHandler) {
        return RouterFunctions.route(
                GET("/prices2/btc/{currencyCode}").and(accept(MediaType.APPLICATION_JSON)),
                requestHandler::price);
    }

    @Bean
    @Order(-2)
    public WebExceptionHandler exceptionHandler() {
        /**
         * Maps default WebClient exception to http response with custom json body.
         * This example only propagates back the ERROR json body that was received back
         * from coinbase-api (demo purposes, since it contains useful info).
         *
         * Example Coinbase-API json error w/useful info:
         * {"errors":[{"id":"invalid_request","message":"Currency is invalid"}]}
         *
         * Note: error handling scenarios can be further customized here
         */
        return (ServerWebExchange exchange, Throwable ex) -> {
            if (ex instanceof WebClientResponseException) {
                // marks the response as complete and forbids writing to it
                final WebClientResponseException webClientException = ((WebClientResponseException) ex);
                DataBuffer bufferPayload;
                if (HttpStatus.BAD_REQUEST.equals(webClientException.getStatusCode())) {
                    //
                    final String responseBody = webClientException.getResponseBodyAsString();
                    byte[] bytes = responseBody.getBytes(StandardCharsets.UTF_8);
                    bufferPayload = exchange.getResponse().bufferFactory().wrap(bytes);
                } else {
                    // generic error for other WebClientResponseException cases
                    byte[] bytes = Optional.of(webClientException.getMessage())
                            .orElse("Unkown Server Error").getBytes(StandardCharsets.UTF_8);
                    bufferPayload = exchange.getResponse().bufferFactory().wrap(bytes);
                }
                // common steps for all http error codes
                exchange.getResponse().setStatusCode(webClientException.getStatusCode());
                exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
                return exchange.getResponse().writeWith(Flux.just(bufferPayload));
            }
            // otherwise emit original exception via Mono.error
            return Mono.error(ex);
        };
    }
}