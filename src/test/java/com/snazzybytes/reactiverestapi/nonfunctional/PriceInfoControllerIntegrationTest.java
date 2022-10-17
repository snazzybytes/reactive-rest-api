package com.snazzybytes.reactiverestapi.nonfunctional;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.snazzybytes.reactiverestapi.dto.PriceInfo;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class PriceInfoControllerIntegrationTest {

    @Autowired
    private WebTestClient webClient;

    /* Annotation-based reactive components (/prices/btc) */
    @ParameterizedTest
    @ValueSource(strings = { "USD", "PLN" })
    public void fetchBitcoinPrice_200(String currency) {
        webClient.mutate()
                .responseTimeout(Duration.ofSeconds(300)).build()
                .get().uri(uri -> uri.path("/prices/btc/").path(currency).build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(PriceInfo.class)
                .value(priceData -> priceData.getCurrency().equals(currency))
                .value(priceData -> assertNotNull(priceData.getSatsPerFiat()))
                .value(priceData -> assertNotNull(priceData.getPrice()));
    }

    @Test
    public void controller_fetchBitcoinPrice_400() {
        final String badCurrency = "BadBoi";
        webClient.mutate()
                .responseTimeout(Duration.ofSeconds(300)).build()
                .get().uri(uri -> uri.path("/prices/btc/").path(badCurrency).build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                // verify error JSON response from PriceRequestRouter.exceptionHandler()
                // (contains coinbase-api json error string with meaningful message/id)
                .jsonPath("$.errors").isNotEmpty()
                .jsonPath("$.errors.length()").isEqualTo(1)
                .jsonPath("$.errors[0].id").isEqualTo("invalid_request")
                .jsonPath("$.errors[0].message").isEqualTo("Currency is invalid");
    }

    /* Functional with RouterFunction + ServerRequest handler (/prices2/btc) */
    @Test
    public void router_fetchBitcoinPrice_400() {
        final String badCurrency = "BadBoi";
        webClient.mutate()
                .responseTimeout(Duration.ofSeconds(300)).build()
                .get().uri(uri -> uri.path("/prices2/btc/").path(badCurrency).build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                // verify error JSON response from PriceRequestRouter.exceptionHandler()
                // (contains coinbase-api json error string with meaningful message/id)
                .jsonPath("$.errors").isNotEmpty()
                .jsonPath("$.errors.length()").isEqualTo(1)
                .jsonPath("$.errors[0].id").isEqualTo("invalid_request")
                .jsonPath("$.errors[0].message").isEqualTo("Currency is invalid");
    }
}
