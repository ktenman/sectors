package com.helmes.recruitment.formhandler.controller;

import com.helmes.recruitment.formhandler.BaseIntegrationTest;
import com.helmes.recruitment.formhandler.domain.Profile;
import com.helmes.recruitment.formhandler.domain.Sector;
import com.helmes.recruitment.formhandler.models.CreateProfileRequest;
import com.helmes.recruitment.formhandler.models.ProfileDTO;
import com.helmes.recruitment.formhandler.service.SessionService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProfileControllerIntegrationTest extends BaseIntegrationTest {
	
	private static final String DEFAULT_NAME = "John Doe";
	
	@MockBean
	private SessionService sessionService;
	
	@Test
	void saveProfile_ShouldReturnSavedProfile() throws Exception {
		UUID sessionId = UUID.randomUUID();
		Mockito.when(sessionService.getSession()).thenReturn(sessionId);
		
		CreateProfileRequest createProfileRequest = CreateProfileRequest.builder()
				.name(DEFAULT_NAME)
				.agreeToTerms(true)
				.sectors(List.of(2L, 22L))
				.build();
		ProfileDTO expectedResponse = new ProfileDTO(1L, DEFAULT_NAME, true, List.of(2L, 22L));
		
		mockMvc.perform(post("/api/profiles")
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
					assertThat(p.getSessionId()).isEqualTo(sessionId);
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
