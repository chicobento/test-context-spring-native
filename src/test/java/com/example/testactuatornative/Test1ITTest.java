package com.example.testactuatornative;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(properties = { "management.server.port=0", "value1=value1" })
@SuppressWarnings("unchecked")
@ActiveProfiles("actuator")
public class Test1ITTest {

	@Test
	public void test1() {
	}
}
