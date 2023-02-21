package com.example.testactuatornative;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatusCode;
import org.springframework.test.context.ActiveProfiles;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT, properties = { "spring.main.web-application-type=REACTIVE",
		"darwin.security.auth-query-parameter=credential",
		"darwin.security.connectors.pkm-connector.pkmEndpoint=http://localhost:${wiremock.server.port}/v1/publicKey",
		"logging.level.root=INFO" })

@SuppressWarnings("unchecked")
@Slf4j
@ActiveProfiles("actuator-same")
public class MatchersSamePortActuatorReactiveTest extends AbstractBaseWebClientTest {

	@Test
	public void matchersHealthEndpointRequest() {
		testClient.get().uri("/actuator/health").exchange().expectStatus().isOk().expectBody(Map.class)
				.consumeWith(result -> assertThat(result.getResponseBody()).containsKeys("status").containsValue("UP"));
	}

	@Test
	public void matchersInfoEndpointRequest() {
		testClient.get().uri("/actuator/info").exchange().expectStatus().isOk().expectBody(Map.class)
				.consumeWith(result -> assertThat(result.getResponseBody()).hasSize(0));
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
