package com.helmes.recruitment.formhandler.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.helmes.recruitment.formhandler.IntegrationTest;
import com.helmes.recruitment.formhandler.domain.Profile;
import com.helmes.recruitment.formhandler.domain.Sector;
import com.helmes.recruitment.formhandler.dto.CreateProfileRequest;
import com.helmes.recruitment.formhandler.dto.PofileDTO;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@IntegrationTest
@Slf4j
class ProfileControllerIntegrationTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private EntityManager entityManager;
	
	@Test
	void saveProfile_ShouldReturnSavedProfile() throws Exception {
		CreateProfileRequest createProfileRequest = CreateProfileRequest.builder()
				.name("John Doe")
				.agreeToTerms(true)
				.sectors(List.of(2L, 22L))
				.build();
		PofileDTO expectedResponse = PofileDTO.builder()
				.id(1L)
				.name("John Doe")
				.agreeToTerms(true)
				.sessionId("dummySessionId")
				.sectors(List.of(2L, 22L))
				.build();
		
		mockMvc.perform(post("/api/profile")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(createProfileRequest)))
				.andExpect(status().isOk())
				.andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));
		
		List<Profile> profiles = entityManager.createQuery(
						"SELECT p FROM Profile p JOIN FETCH p.sectors", Profile.class)
				.getResultList();
		assertThat(profiles).isNotEmpty()
				.first()
				.satisfies(p -> {
					assertThat(p.getName()).isEqualTo(createProfileRequest.getName());
					assertThat(p.getAgreeToTerms()).isEqualTo(createProfileRequest.getAgreeToTerms());
					assertThat(p.getSessionId()).isEqualTo("dummySessionId");
					assertThat(p.getSectors())
							.hasSize(2)
							.extracting(Sector::getId, Sector::getName)
							.containsExactlyInAnyOrder(
									tuple(2L, "Service"),
									tuple(22L, "Tourism")
							);
				});
	}
	
}
