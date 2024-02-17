package com.helmes.recruitment.formhandler.controller;

import com.helmes.recruitment.formhandler.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IntegrationTest
class SectorControllerIntegrationTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ResourceLoader resourceLoader;
	
	@Test
	void whenGetAllSectors_thenReturns200AndSectorList() throws Exception {
		Resource resource = resourceLoader.getResource("classpath:sectors.json");
		
		ResultActions resultActions = mockMvc.perform(get("/api/sectors").contentType(APPLICATION_JSON));
		
		resultActions.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
				.andExpect(content().encoding(UTF_8))
				.andExpect(content().json(resource.getContentAsString(UTF_8)));
	}
	
}
