package com.helmes.recruitment.formhandler.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.helmes.recruitment.formhandler.IntegrationTest;
import com.helmes.recruitment.formhandler.domain.Profile;
import com.helmes.recruitment.formhandler.domain.Sector;
import com.helmes.recruitment.formhandler.models.CreateProfileRequest;
import com.helmes.recruitment.formhandler.models.ProfileDTO;
import com.helmes.recruitment.formhandler.repository.ProfileRepository;
import com.helmes.recruitment.formhandler.service.SessionService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IntegrationTest
class ProfileControllerIntegrationTest {
	
	private static final String DEFAULT_NAME = "John Doe";
	private static final UUID DEFAULT_SESSION_ID = UUID.randomUUID();
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private EntityManager entityManager;
	
	@Autowired
	private ProfileRepository profileRepository;
	
	@MockBean
	private SessionService sessionService;
	
	@BeforeEach
	void setUp() {
		when(sessionService.getSession()).thenReturn(DEFAULT_SESSION_ID);
	}
	
	@Test
	void saveProfile_givenValidProfileData_thenReturnsSavedProfile() throws Exception {
		CreateProfileRequest createProfileRequest = CreateProfileRequest.builder()
				.name(DEFAULT_NAME)
				.agreeToTerms(true)
				.sectors(List.of(2L, 22L))
				.build();
		ProfileDTO expectedResponse = new ProfileDTO(1L, DEFAULT_NAME, true, List.of(2L, 22L));
		
		mockMvc.perform(post("/api/profiles")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(createProfileRequest)))
				.andExpect(status().isCreated())
				.andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));
		
		List<Profile> profiles = entityManager.createQuery(
						"SELECT p FROM Profile p JOIN FETCH p.sectors", Profile.class)
				.getResultList();
		
		Instant now = Instant.now();
		assertThat(profiles).isNotEmpty()
				.first()
				.satisfies(p -> {
					assertThat(p.getName()).isEqualTo(DEFAULT_NAME);
					assertThat(p.getAgreeToTerms()).isTrue();
					assertThat(p.getSessionId()).isEqualTo(DEFAULT_SESSION_ID);
					assertThat(p.getSectors())
							.hasSize(2)
							.extracting(Sector::getId, Sector::getName)
							.containsExactlyInAnyOrder(
									tuple(2L, "Service"),
									tuple(22L, "Tourism")
							);
					assertThat(p.getCreatedAt()).isNotNull().isBefore(now);
					assertThat(p.getUpdatedAt()).isNotNull().isBefore(now);
				});
	}
	
	@Test
	void updateProfile_givenValidData_thenReturnsUpdatedProfile() throws Exception {
		Profile profile = new Profile();
		profile.setSessionId(DEFAULT_SESSION_ID);
		profile.setName("Old Name");
		profile.setAgreeToTerms(true);
		profileRepository.save(profile);
		
		List<Long> sectors = List.of(1L, 2L, 8L);
		CreateProfileRequest createProfileRequest = CreateProfileRequest.builder()
				.name(DEFAULT_NAME)
				.agreeToTerms(true)
				.sectors(sectors)
				.build();
		ProfileDTO expectedResponse = new ProfileDTO(1L, DEFAULT_NAME, true, sectors);
		
		mockMvc.perform(post("/api/profiles")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(createProfileRequest)))
				.andExpect(status().isOk())
				.andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));
		
		List<Profile> profiles = entityManager.createQuery(
						"SELECT p FROM Profile p JOIN FETCH p.sectors", Profile.class)
				.getResultList();
		
		Instant now = Instant.now();
		assertThat(profiles).isNotEmpty()
				.first()
				.satisfies(p -> {
					assertThat(p.getName()).isEqualTo(DEFAULT_NAME);
					assertThat(p.getAgreeToTerms()).isTrue();
					assertThat(p.getSessionId()).isEqualTo(DEFAULT_SESSION_ID);
					assertThat(p.getSectors())
							.hasSize(3)
							.extracting(Sector::getId, Sector::getName)
							.containsExactlyInAnyOrder(
									tuple(2L, "Service"),
									tuple(8L, "Wood"),
									tuple(1L, "Manufacturing")
							);
					assertThat(p.getCreatedAt()).isNotNull().isBefore(now);
					assertThat(p.getUpdatedAt()).isNotNull().isBefore(now);
				});
	}
	
}
