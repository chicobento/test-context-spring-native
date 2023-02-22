package com.example.testactuatornative;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(properties = { "management.server.port=0", "value2=value2" })
@SuppressWarnings("unchecked")
@ActiveProfiles("actuator")
public class Test2ITTest {

	@Test
	public void test1() {
	}
}
