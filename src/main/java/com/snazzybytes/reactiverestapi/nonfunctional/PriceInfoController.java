package com.snazzybytes.reactiverestapi.nonfunctional;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.snazzybytes.reactiverestapi.dto.PriceInfo;
import com.snazzybytes.reactiverestapi.dto.coinbase.PriceData;
import com.snazzybytes.reactiverestapi.service.CoinbaseWebClient;

import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/prices")
@Log4j2
public class PriceInfoController {

    private CoinbaseWebClient webClient;

    public PriceInfoController(CoinbaseWebClient webClient) {
        this.webClient = webClient;
    }

    @GetMapping("/btc/{currencyCode}")
    private Mono<PriceInfo> fetchBitcoinPrice(@PathVariable String currencyCode) {
        final StopWatch watch = new StopWatch();
        watch.start();
        log.info("Received request for currencyCode={}", currencyCode);
        Mono<PriceData> priceDataMono = webClient.getBtcPrice(currencyCode);
        return priceDataMono
                .flatMap(priceData -> {
                    final BigDecimal amount = new BigDecimal(priceData.getAmount()).setScale(2, RoundingMode.HALF_UP);
                    return Mono.just(PriceInfo.builder()
                            .price(amount)
                            .currency(priceData.getCurrency())
                            // calculate sats per fiat
                            .satsPerFiat(BigDecimal.valueOf(100000000L).divide(amount, RoundingMode.HALF_UP))
                            .build())
                            .doFinally(type -> {
                                watch.stop();
                                log.info("Responded in {} milliseconds", watch.getTotalTimeMillis());
                            });
                });
    }
}