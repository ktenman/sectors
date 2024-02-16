package com.helmes.recruitment.formhandler.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ObjectMapperConfiguration {
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
	
	@Bean public ObjectMapper objectMapper() { return OBJECT_MAPPER; }
}
