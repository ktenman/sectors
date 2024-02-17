package com.helmes.recruitment.formhandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.web.servlet.MockMvc;

@IntegrationTest
public abstract class BaseIntegrationTest {
	
	@Autowired
	protected MockMvc mockMvc;
	
	@Autowired
	protected ResourceLoader resourceLoader;
	
	@Autowired
	protected ObjectMapper objectMapper;
	
	@Autowired
	protected EntityManager entityManager;
	
}
