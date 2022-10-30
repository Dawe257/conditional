package com.dzhenetl.conditional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.testcontainers.containers.GenericContainer;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ConditionalApplicationTests {

	@Autowired
	TestRestTemplate restTemplate;

	private static final GenericContainer<?> devApp = new GenericContainer<>("devapp")
			.withExposedPorts(8080);
	private static final GenericContainer<?> prodApp = new GenericContainer<>("prodapp")
			.withExposedPorts(8081);

	@BeforeAll
	public static void setUp() {
		devApp.start();
		prodApp.start();
	}

	@Test
	void contextLoads() {
		String devUrl = "http://localhost:" + devApp.getMappedPort(8080) + "/profile";
		String prodUrl = "http://localhost:" + prodApp.getMappedPort(8081) + "/profile";

		ResponseEntity<String> devResponse = restTemplate.getForEntity(devUrl, String.class);
		ResponseEntity<String> prodResponse = restTemplate.getForEntity(prodUrl, String.class);

		Assertions.assertTrue(devResponse.getStatusCode().is2xxSuccessful());
		Assertions.assertTrue(prodResponse.getStatusCode().is2xxSuccessful());
		Assertions.assertEquals(devResponse.getBody(), "Current profile is dev");
		Assertions.assertEquals(prodResponse.getBody(), "Current profile is production");
	}
}