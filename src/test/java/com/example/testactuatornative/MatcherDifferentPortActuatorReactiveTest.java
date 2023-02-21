package com.example.testactuatornative;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalManagementPort;
import org.springframework.http.HttpStatusCode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT, properties = { "spring.main.web-application-type=REACTIVE",
		"darwin.security.auth-query-parameter=credential", "management.server.port=0",
		"darwin.security.connectors.pkm-connector.pkmEndpoint=http://localhost:${wiremock.server.port}/v1/publicKey" })
@SuppressWarnings("unchecked")
@ActiveProfiles("actuator")
public class MatcherDifferentPortActuatorReactiveTest extends AbstractBaseWebClientTest {

	WebTestClient actuatorsTestClient;

	@LocalManagementPort
	private Integer localManagementPort;

	@BeforeEach
	public void setMatcherPort() {
		actuatorsTestClient = WebTestClient.bindToServer().baseUrl("http://localhost:" + localManagementPort).build();
	}

	@Test
	public void matchersHealthEndpointRequest() {
		testClient.get().uri("/actuator/health").exchange().expectStatus().isNotFound().expectBody(Map.class)
				.consumeWith(result -> assertEquals(result.getStatus(), HttpStatusCode.valueOf(404)));
	}

	@Test
	public void matchersInfoEndpointRequest() {
		testClient.get().uri("/actuator/info").exchange().expectStatus().isNotFound().expectBody(Map.class)
				.consumeWith(result -> assertEquals(result.getStatus(), HttpStatusCode.valueOf(404)));
	}

	@Test
	public void matchersInfoEndpointRequestToActuatorPort() {
		actuatorsTestClient.get().uri("/actuator/info").exchange().expectStatus().isOk().expectBody(Map.class)
				.consumeWith(result -> assertThat(result.getResponseBody()).hasSize(0));

	}

	@Test
	public void matchersPostEndpointRequestToActuatorPort() {
		actuatorsTestClient.post().uri("/actuator/info").exchange().expectStatus().is4xxClientError()
				.expectBody(Map.class).consumeWith(result -> assertEquals(405, result.getStatus().value()));
	}

	@Test
	public void matchersHealthEndpointRequestToActuatorPort() {
		actuatorsTestClient.get().uri("/actuator/health").exchange().expectStatus().isOk().expectBody(Map.class)
				.consumeWith(result -> assertThat(result.getResponseBody()).containsKeys("status").containsValue("UP"));
	}

	@Test
	public void matchersWhiteListRequest() {
		testClient.get().uri("/public-access/headers").exchange().expectStatus().isOk().expectBody(Map.class)
				.consumeWith(result -> assertThat(result.getResponseBody()).hasSize(1).containsKeys("headers"));
	}

	@Test
	public void matchers401Error() {
		testClient.get().uri("/headers").exchange().expectStatus().isOk().expectBody(Map.class)
				.consumeWith(result -> assertEquals(result.getStatus(), HttpStatusCode.valueOf(200)));
	}

}
