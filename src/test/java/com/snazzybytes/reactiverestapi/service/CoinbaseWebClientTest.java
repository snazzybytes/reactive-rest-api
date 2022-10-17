package com.snazzybytes.reactiverestapi.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.snazzybytes.reactiverestapi.dto.coinbase.PriceData;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import reactor.core.publisher.Mono;

/**
 * By using MockWebServer it is communicating over an HTTP connection.
 * This means slightly slower than a pure unit test but negligible.
 */
@ExtendWith(SpringExtension.class)
class CoinbaseWebClientTest {

    private MockWebServer mockWebServer;
    private CoinbaseWebClient client;

    @BeforeEach
    void setup() throws IOException {
        this.mockWebServer = new MockWebServer();
        this.mockWebServer.start();
        this.client = new CoinbaseWebClient(WebClient.builder(), mockWebServer.url("/").toString());
    }

    @Test
    void test_getBtcPrice_happyPath() throws InterruptedException {
        final String currency = "USD";
        MockResponse mockResponse = new MockResponse()
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .setBody("{\"data\":{\"base\":\"BTC\",\"currency\":\"USD\",\"amount\":\"19146.05\"}}");

        mockWebServer.enqueue(mockResponse);

        Mono<PriceData> result = client.getBtcPrice(currency);
        // PriceData priceData = result.block();
        result.subscribe(resp -> {
            assertEquals("19146.05", resp.getAmount());
            assertEquals("USD", resp.getCurrency());
            assertEquals("BTC", resp.getBase());
        });

        RecordedRequest request = mockWebServer.takeRequest();
        assertEquals("GET", request.getMethod());
        assertEquals("/?currency=" + currency, request.getPath());
    }

    @Test
    void test_getByPrice_sadPath() throws InterruptedException {
        final String badCurrency = "badboi";
        MockResponse mockResponse = new MockResponse()
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .setBody("{\"errors\":[{\"id\":\"invalid_request\",\"message\":\"Currency is invalid\"}]}")
                .setResponseCode(HttpStatus.BAD_REQUEST.value());

        mockWebServer.enqueue(mockResponse);

        assertThrows(WebClientResponseException.class, () -> client.getBtcPrice(badCurrency).block());

    }
}
