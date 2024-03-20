package com.helmes.recruitment.formhandler.controller;

import com.helmes.recruitment.formhandler.configuration.logging.aspect.Loggable;
import com.helmes.recruitment.formhandler.domain.Profile;
import com.helmes.recruitment.formhandler.mapper.ProfileMapper;
import com.helmes.recruitment.formhandler.models.CreateProfileRequest;
import com.helmes.recruitment.formhandler.models.ProfileDTO;
import com.helmes.recruitment.formhandler.models.ServiceResult;
import com.helmes.recruitment.formhandler.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/profiles")
@RequiredArgsConstructor
public class ProfileController {
	
	private final ProfileService profileService;
	
	@PostMapping
	@Loggable
	public ResponseEntity<ProfileDTO> saveProfile(@RequestBody @Valid CreateProfileRequest createProfileRequest) {
		ServiceResult<Profile> serviceResult = profileService.saveProfile(createProfileRequest);
		HttpStatus status = switch (serviceResult.outcome()) {
			case CREATED -> HttpStatus.CREATED;
			case UPDATED -> HttpStatus.OK;
		};
		Profile profile = serviceResult.body();
		return ResponseEntity.status(status).body(ProfileMapper.toDTO(profile));
	}
	
	@GetMapping
	@Loggable
	public ProfileDTO getProfile() {
		Profile profile = profileService.getProfile();
		return ProfileMapper.toDTO(profile);
	}
	
}
