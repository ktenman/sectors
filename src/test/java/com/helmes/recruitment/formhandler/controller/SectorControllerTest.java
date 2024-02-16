package com.helmes.recruitment.formhandler.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.helmes.recruitment.formhandler.IntegrationTest;
import com.helmes.recruitment.formhandler.dto.SectorDTO;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IntegrationTest
class SectorControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ResourceLoader resourceLoader;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	
	@Test
	void whenGetAllSectors_thenReturns200AndSectorList() throws Exception {
		MockHttpServletResponse response = mockMvc.perform(get("/api/sectors")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(content().encoding(StandardCharsets.UTF_8))
				.andReturn()
				.getResponse();
		
		List<SectorDTO> actualResponse = objectMapper.readValue(response.getContentAsString(), new TypeReference<>() {
		});
		
		Resource resource = resourceLoader.getResource("classpath:sectors.json");
		List<SectorDTO> expectedResponse = objectMapper.readValue(resource.getInputStream(), new TypeReference<>() {
		});
		compareSectorLists(actualResponse, expectedResponse);
	}
	
	private void compareSectorLists(List<SectorDTO> actual, List<SectorDTO> expected) {
		assertThat(actual).usingRecursiveFieldByFieldElementComparatorIgnoringFields("children").containsExactlyInAnyOrderElementsOf(expected);
		expected.forEach(expectedSector -> {
			SectorDTO actualSector = actual.stream()
					.filter(a -> a.getId().equals(expectedSector.getId()))
					.findFirst()
					.orElseThrow(() -> new AssertionError("Sector with ID " + expectedSector.getId() + " not found"));
			if (expectedSector.getChildren() != null && !expectedSector.getChildren().isEmpty()) {
				compareSectorLists(expectedSector.getChildren(), actualSector.getChildren());
			}
		});
	}
	
}
