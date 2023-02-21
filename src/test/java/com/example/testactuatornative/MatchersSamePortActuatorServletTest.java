package com.example.testactuatornative;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = { "spring.main.web-application-type=SERVLET",
		"darwin.security.auth-query-parameter=credential", "management.server.port=0",
		"darwin.security.connectors.pkm-connector.pkmEndpoint=http://localhost:${wiremock.server.port}/v1/publicKey" })
@AutoConfigureMockMvc
public class MatchersSamePortActuatorServletTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void matchersHealthEndpointRequest() throws Exception {
		mockMvc.perform(get("/actuator/health")).andExpect(status().isUnauthorized())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON));
	}

	@Test
	public void matchersInfoEndpointRequest() throws Exception {
		mockMvc.perform(get("/actuator/info")).andExpect(status().isUnauthorized())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON));
	}

	@Test
	public void matchersWhiteListRequest() throws Exception {
		mockMvc.perform(get("/public-access/headers-servlet")).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(content().string("{\"headers\":{}}"));
	}

	@Test
	public void matchers401Error() throws Exception {
		mockMvc.perform(get("/headers")).andExpect(status().isUnauthorized())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON));
	}

}
