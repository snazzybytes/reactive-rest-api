# Reactive Rest API with Spring Boot + WebFlux

[![Java CI with Maven](https://github.com/snazzybytes/reactive-rest-api/actions/workflows/maven.yml/badge.svg)](https://github.com/snazzybytes/reactive-rest-api/actions/workflows/maven.yml)

---

## Overview

This is an boilerplate example of **Reactive Rest API with Spring Boot + WebFlux** for modern non-blocking rest api development.

It diplays the basic usage of reactive web components via 2 different approaches (both endpoints behave exactly same but are wired up differently).

### 1st API endpoint => "/prices/btc/{currency}"

- annotation-based reactive components: RestController/RequestMapping/GetMapping and WebClient (the good ole way)

### 2nd API endpoint => "/prices2/btc/{currency}"

- functional routing and handling: Router returns ServerResponse, Handler processes ServerRequest

The sample service calls out to Coinbase-API to get PriceData, and returns PriceInfo model containing "price", "currency", and "satsPerFiat":

- price: decimal amount with 2-decimal points in requested currency
- currency: requested currency code
- satsPerFiat: number of satoshis (unit of bitcoin) per requested fiat currency unit

---

## Highlights

- Spring Boot/WebFlux:
  - uses @Value for constructor injection of config properties from application.yaml
  - application.yaml defines multi-environment properties in single file (can be split up into application-test.yml, application-dev.yml, etc)
  - simple StopWatch to measure time between when Mono is subscribed to, and when Mono completes
- JUnit5 enabled (see pom.xml's "org.junit.jupiter" dependencies)
  - parameterized unit tests via @ParameterizedTest and @ValueSource
  - testing WebClient with MockWebServer
- integration tests:
  - uses WebTestClient over http connection to test server for success/failure (can be used with mock req/resp but NO mocks are used in this case)
  - WebTestClient's benefit is the fluent API for verifying responses (expectBody() etc.. see tests)
- logging:
  - log4j2 enabled via "spring-boot-starter-log4j2" (instead of logback spring default)
  - log4j2-spring.xml defines log patterns (xml could be replaced with YAML/JSON if needed)
- maven :
  - common plugins configured: maven-dependency-plugin/maven-enforcer-plugin/maven-checkstyle-plugin
- metrics :
  - Spring's Actuator w/Micrometer metrics including Prometheus
  - spring actuator endpoints are enabled via application.yaml, and maven dependency (see pom.xml) to enable prometheus
- spring-boot-devtools (aids local development)
- Docker: sample Dockerfile and docker-compose included

Custom Log4j2 logging:
![Custom Logging](/assets/custom-logging.png)

---

# Usage

## Run the app

You can run spring-boot ReactiveRestApiApplication in your favorite IDE like VSCode. Dockerfile and docker-compose included for convenience (defaults from .env file will be used if not passed).

### Run with docker-compse:

```
$ docker-compose up
```

Alternatively, you can also pass in custom JAVA_OPTS like so:

```
$ JAVA_OPTS="-Xmx512m" docker-compose up
```

### Run with plain "docker" command:

```
docker run -e JAVA_OPTS="-Xmx512m" -p8080:8080 snazzybytes/reactive-rest-api
```

## Call the API

### Request:

```bash
# 2 reactive endpoints w/ same response
GET http://localhost:8080/prices/btc/USD

GET http://localhost:8080/prices2/btc/EUR
```

### Response:

```json
// success - PriceInfo response
{
  "price": "19764.81",
  "currency": "EUR",
  "satsPerFiat": "5059"
}
```

```json
// error - bad request  (custom exception handler response with sample json)
{
  "errors": [
    {
      "id": "invalid_request",
      "message": "Currency is invalid"
    }
  ]
}
```

---

## Reference Documentation

- [Spring Reactive Web](https://docs.spring.io/spring-boot/docs/2.7.4/reference/htmlsingle/#web.reactive)
- [Spring WebFlux reference](https://docs.spring.io/spring-framework/docs/current/reference/html/web-reactive.html#webflux-reactive-api)
- [Coinbase API: Price Data(free w/Rate limiting)](https://docs.cloud.coinbase.com/sign-in-with-coinbase/docs/price-data)
