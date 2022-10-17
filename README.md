# Reactive Rest API with Spring Boot + WebFlux

[![Java CI with Maven](https://github.com/snazzybytes/reactive-rest-api/actions/workflows/maven.yml/badge.svg)](https://github.com/snazzybytes/reactive-rest-api/actions/workflows/maven.yml)

This is an boilerplate example of **Reactive Rest API with Spring Boot + WebFlux** for modern non-blocking rest api development.

It diplays the basic usage of reactive web components via 2 different approaches (both behave exactly same to showcase each):

- 1st endpoint: /prices/btc/{currency}

  - annotation-based reactive components: RestController/RequestMapping/GetMapping and WebClient (the good ole way)

- 2nd endpoint: /prices2/btc/{currency}
  - functional routing and handling (Router returns ServerResponse, Handler processes ServerRequest)

#### Highlights

- Spring Boot/WebFlux:
  - uses @Value for constructor injection of config properties from application.yaml
  - application.yaml defines multi-environment properties in single file (can be split up into application-test.yml, application-dev.yml, etc)
  - simple StopWatch to measure time between when Mono is subscribed to, and when Mono completes
- JUnit5 enabled (see pom.xml's "org.junit.jupiter" dependencies)
  - parameterized tests via @ParameterizedTest and @ValueSource
  - testing WebClient using MockWebServer
- integration tests:
  - uses WebTestClient over http connection to test server for success/failure (can be used with mock req/resp but NO mocks are used in this case)
  - WebTestClient's benefit is the fluent API for verifying responses (expectBody() etc.. see tests)
- logging:
  - log4j2 enabled via "spring-boot-starter-log4j2" (instead of logback spring default)
  - log4j2-spring.xml defines log patterns (xml could be replaced with YAML/JSON if needed)
- maven :
  - some example plugins configured - maven-dependency-plugin/maven-enforcer-plugin/maven-checkstyle-plugin
- metrics :
  - Spring's Actuator w/Micrometer metrics including Prometheus
  - spring actuator endpoints are enabled via application.yaml, and maven dependency (see pom.xml) to enable prometheus
- spring-boot-devtools (aids local development)

Custom Log4j2 logging:
![Custom Logging](/assets/custom-logging.png)

Success (PriceInfo response):

- GET http://localhost:8080/prices2/btc/USD

![Success Response](/assets/success.png)

Failure (custom exception handler response with sample json):

- GET http://localhost:8080/prices2/btc/DummyBadCode

![Failure Response](/assets/failure.png)

### Reference Documentation

- [Spring Reactive Web](https://docs.spring.io/spring-boot/docs/2.7.4/reference/htmlsingle/#web.reactive)
- [Spring WebFlux reference](https://docs.spring.io/spring-framework/docs/current/reference/html/web-reactive.html#webflux-reactive-api)
- [Coinbase API: Price Data(free w/Rate limiting)](https://docs.cloud.coinbase.com/sign-in-with-coinbase/docs/price-data)
