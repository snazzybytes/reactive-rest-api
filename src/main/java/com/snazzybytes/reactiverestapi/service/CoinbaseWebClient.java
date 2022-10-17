package com.snazzybytes.reactiverestapi.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.reactive.function.client.WebClient;

import com.snazzybytes.reactiverestapi.dto.coinbase.PriceData;
import com.snazzybytes.reactiverestapi.dto.coinbase.PriceDataResp;

import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;

@Component
@Log4j2
public class CoinbaseWebClient {

    private final WebClient client;

    public CoinbaseWebClient(WebClient.Builder builder, @Value("${coinbaseapi.baseurl}") String baseUrl) {
        this.client = builder.baseUrl(baseUrl).build();
    }

    public Mono<PriceData> getBtcPrice(String currency) {
        final StopWatch watch = new StopWatch();
        return this.client.get()
                .uri(uriBuilder -> uriBuilder.queryParam("currency", currency).build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                /**
                 * In case of http error the "bodyToMono" throws default WebClientExeption
                 * containing original Coinbase API's json error body string
                 * (which can be used as needed for implementing WebExceptionHandler).
                 *
                 * To customize error handling: use "onStatus(HttpStatus::isError, response)"
                 */
                .bodyToMono(PriceDataResp.class)
                .map(PriceDataResp::getData)
                .doOnSubscribe(e -> watch.start())
                .doFinally(type -> {
                    watch.stop();
                    log.info("Responded in {} milliseconds for SignalType={}", watch.getTotalTimeMillis(), type.name());
                });
    }

}
