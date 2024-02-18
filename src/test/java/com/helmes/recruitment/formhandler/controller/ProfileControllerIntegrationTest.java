package com.helmes.recruitment.formhandler.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.helmes.recruitment.formhandler.IntegrationTest;
import com.helmes.recruitment.formhandler.configuration.exception.GlobalExceptionHandler.ApiError;
import com.helmes.recruitment.formhandler.domain.Profile;
import com.helmes.recruitment.formhandler.domain.Sector;
import com.helmes.recruitment.formhandler.models.CreateProfileRequest;
import com.helmes.recruitment.formhandler.models.ProfileDTO;
import com.helmes.recruitment.formhandler.service.SessionService;
import jakarta.persistence.EntityManager;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IntegrationTest
class ProfileControllerIntegrationTest {
	
	private static final String DEFAULT_NAME = "John Doe";
	private static final String LONG_NAME_65_CHARS = RandomStringUtils.randomAlphabetic(65);
	private static final UUID DEFAULT_SESSION_ID = UUID.fromString("80ccee8e-c7ef-4a66-9871-a399b3870132");
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private EntityManager entityManager;
	
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
		
		List<Profile> profiles = findALLProfiles();
		
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
	@Transactional
	void updateProfile_givenValidData_thenReturnsUpdatedProfile() throws Exception {
		persist(aProfile());
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
		
		List<Profile> profiles = findALLProfiles();
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

	@Test
	void saveProfile_withInvalidProfileData_returnsMultipleValidationErrors() throws Exception {
		MvcResult mvcResult = mockMvc.perform(post("/api/profiles")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{}"))
				.andExpect(status().isBadRequest())
				.andReturn();
		
		ApiError apiError = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ApiError.class);
		assertThat(apiError.getMessage()).isEqualTo("Validation error");
		assertThat(apiError.getDebugMessage()).isEqualTo("One or more fields have an error");
		assertThat(apiError.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(apiError.getValidationErrors()).hasSize(3)
				.containsEntry("name", "Name is required")
				.containsEntry("agreeToTerms", "Agreement to terms is mandatory")
				.containsEntry("sectors", "At least one sector must be selected");
	}
	
	@Test
	void saveProfile_withMissingRequestBody_throwsInternalServerError() throws Exception {
		MvcResult mvcResult = mockMvc.perform(post("/api/profiles")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError())
				.andReturn();
		
		ApiError apiError = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ApiError.class);
		assertThat(apiError.getMessage()).contains("Required request body is missing");
		assertThat(apiError.getDebugMessage()).isEqualTo("An internal error occurred");
		assertThat(apiError.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
		assertThat(apiError.getValidationErrors()).isNull();
	}
	
	@Test
	void saveProfile_withInvalidProfileData_returnsValidationErrorForNameLength() throws Exception {
		CreateProfileRequest createProfileRequest = CreateProfileRequest.builder()
				.name(LONG_NAME_65_CHARS)
				.agreeToTerms(true)
				.sectors(List.of(1L))
				.build();
		
		ResultActions resultActions = mockMvc.perform(post("/api/profiles")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(createProfileRequest)))
				.andExpect(status().isBadRequest());
		
		ApiError apiError = objectMapper.readValue(resultActions.andReturn().getResponse().getContentAsString(), ApiError.class);
		assertThat(apiError.getMessage()).isEqualTo("Validation error");
		assertThat(apiError.getDebugMessage()).isEqualTo("One or more fields have an error");
		assertThat(apiError.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(apiError.getValidationErrors()).hasSize(1)
				.containsKey("name")
				.containsValue("Name must not exceed 64 characters");
	}
	
	@Test
	void requestProfile_whenProfileNotFound_shouldReturnForbiddenStatus() throws Exception {
		MvcResult mvcResult = mockMvc.perform(get("/api/profiles")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isForbidden())
				.andReturn();
		
		ApiError apiError = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ApiError.class);
		assertThat(apiError.getMessage()).isEqualTo("Profile not found for sessionId: 80ccee8e-c7ef-4a66-9871-a399b3870132");
		assertThat(apiError.getDebugMessage()).isEqualTo("Access to the requested resource is denied");
		assertThat(apiError.getStatus()).isEqualTo(HttpStatus.FORBIDDEN);
		assertThat(apiError.getValidationErrors()).isNull();
	}
	
	@Test
	@Transactional
	void retrieveProfile_whenProfileExists_shouldReturnProfileDetailsSuccessfully() throws Exception {
		Profile profile = aProfile();
		persist(profile);
		
		MvcResult mvcResult = mockMvc.perform(get("/api/profiles")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn();
		
		ProfileDTO profileDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ProfileDTO.class);
		assertThat(profileDTO.id()).isEqualTo(1L);
		assertThat(profileDTO.name()).isEqualTo("Old Name");
		assertThat(profileDTO.agreeToTerms()).isTrue();
		Long expectedSectorId = profile.getSectors().stream().map(Sector::getId).findFirst().orElseThrow();
		assertThat(profileDTO.sectors()).singleElement().isEqualTo(expectedSectorId);
	}
	
	private Profile aProfile() {
		Profile profile = new Profile();
		profile.setSessionId(DEFAULT_SESSION_ID);
		profile.setName("Old Name");
		profile.setAgreeToTerms(true);
		
		Sector sector = new Sector();
		sector.setName("Cars Manufacturing");
		profile.setSectors(Set.of(sector));
		
		return profile;
	}
	
	private List<Profile> findALLProfiles() {
		return entityManager.createQuery(
				"SELECT p FROM Profile p JOIN FETCH p.sectors", Profile.class
		).getResultList();
	}
	
	private void persist(Object entity) {
		entityManager.persist(entity);
		entityManager.flush();
	}
	
}
