package com.helmes.recruitment.formhandler.controller;

import com.helmes.recruitment.formhandler.configuration.logging.aspect.Loggable;
import com.helmes.recruitment.formhandler.models.CreateProfileRequest;
import com.helmes.recruitment.formhandler.models.ProfileDTO;
import com.helmes.recruitment.formhandler.models.ServiceResult;
import com.helmes.recruitment.formhandler.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
		ServiceResult<ProfileDTO> serviceResult = profileService.saveProfile(createProfileRequest);
		return ResponseEntity.status(serviceResult.status()).body(serviceResult.body());
	}
	
	@GetMapping
	@Loggable
	public ProfileDTO getProfile() {
		return profileService.getProfile();
	}
	
}
