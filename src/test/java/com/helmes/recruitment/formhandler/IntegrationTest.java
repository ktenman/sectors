package com.helmes.recruitment.formhandler;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(initializers = Initializer.class)
@Sql(scripts = "/clear_database.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public @interface IntegrationTest {
}

class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
	
	private static final PostgreSQLContainer<?> POSTGRES_DB_CONTAINER = new PostgreSQLContainer<>("postgres:16.2-alpine");
	private static final GenericContainer<?> REDIS_CONTAINER = new GenericContainer<>(DockerImageName.parse("redis:7.2-alpine"))
			.withExposedPorts(6379);
	
	static {
		REDIS_CONTAINER.start();
		POSTGRES_DB_CONTAINER.start();
	}
	
	@Override
	public void initialize(ConfigurableApplicationContext applicationContext) {
		TestPropertyValues.of(
				"spring.data.redis.host=" + REDIS_CONTAINER.getHost(),
				"spring.data.redis.port=" + REDIS_CONTAINER.getFirstMappedPort(),

				"spring.datasource.url=" + POSTGRES_DB_CONTAINER.getJdbcUrl(),
				"spring.datasource.username=" + POSTGRES_DB_CONTAINER.getUsername(),
				"spring.datasource.password=" + POSTGRES_DB_CONTAINER.getPassword()
		).applyTo(applicationContext.getEnvironment());
	}
}

