package com.example.testactuatornative;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

/**
 * @author Santander Technology
 */
@TestPropertySource(properties = { "wiremock.server.port=0" })
public abstract class AbstractBaseWebClientTest {

	protected static final Duration DURATION = Duration.ofSeconds(120);

	@LocalServerPort
	protected int port = 0;

	protected WebTestClient testClient;

	protected WebClient webClient;

	protected String baseUri;

	@BeforeEach
	public void setup() {

		baseUri = "http://localhost:" + port;

		this.testClient = WebTestClient.bindToServer().baseUrl(baseUri).responseTimeout(DURATION).build();
	}

}
