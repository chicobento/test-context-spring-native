package com.example.testactuatornative;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT, properties = { "spring.main.web-application-type=REACTIVE", "wiremock.server.port=0"  })
public class SamePortTests {

    @LocalServerPort
    int port = 0;

    String baseUri;

    WebTestClient testClient;

    @BeforeEach
    public void setup() {

        baseUri = "http://localhost:" + port;

        this.testClient = WebTestClient.bindToServer().baseUrl(baseUri).build();
    }

    @Test
    public void matchersHealthEndpointRequest() {
        testClient.get().uri("/actuator/health").exchange().expectStatus().isOk().expectBody(Map.class)
                .consumeWith(result -> assertThat(result.getResponseBody()).containsKeys("status").containsValue("UP"));
    }

}
