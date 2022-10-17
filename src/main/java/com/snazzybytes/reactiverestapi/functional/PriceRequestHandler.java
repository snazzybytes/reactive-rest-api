package com.snazzybytes.reactiverestapi.functional;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.snazzybytes.reactiverestapi.dto.coinbase.PriceData;
import com.snazzybytes.reactiverestapi.service.CoinbaseWebClient;

import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;

@Component
@Log4j2
public class PriceRequestHandler {

    private CoinbaseWebClient webClient;

    public PriceRequestHandler(CoinbaseWebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<ServerResponse> price(ServerRequest request) {
        final StopWatch watch = new StopWatch();
        watch.start();
        final String currencyCode = request.pathVariable("currencyCode");
        log.info("Processing request for currencyCode={}", currencyCode);
        Mono<PriceData> priceDataMono = webClient.getBtcPrice(currencyCode);
        return priceDataMono.flatMap(
                priceData -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(priceData)));
    }

}