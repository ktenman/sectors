package com.helmes.recruitment.formhandler.controller;

import com.helmes.recruitment.formhandler.BaseIntegrationTest;
import com.helmes.recruitment.formhandler.domain.Profile;
import com.helmes.recruitment.formhandler.domain.Sector;
import com.helmes.recruitment.formhandler.dto.CreateProfileRequest;
import com.helmes.recruitment.formhandler.dto.PofileDTO;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProfileControllerIntegrationTest extends BaseIntegrationTest {
	
	private static final String DEFAULT_NAME = "John Doe";
	
	@Test
	void saveProfile_ShouldReturnSavedProfile() throws Exception {
		CreateProfileRequest createProfileRequest = CreateProfileRequest.builder()
				.name(DEFAULT_NAME)
				.agreeToTerms(true)
				.sectors(List.of(2L, 22L))
				.build();
		PofileDTO expectedResponse = PofileDTO.builder()
				.id(1L)
				.name(DEFAULT_NAME)
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
					assertThat(p.getName()).isEqualTo(DEFAULT_NAME);
					assertThat(p.getAgreeToTerms()).isTrue();
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
