package com.helmes.recruitment.formhandler.controller;

import com.helmes.recruitment.formhandler.configuration.logging.aspect.Loggable;
import com.helmes.recruitment.formhandler.models.CreateProfileRequest;
import com.helmes.recruitment.formhandler.models.ProfileDTO;
import com.helmes.recruitment.formhandler.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
@RestController
@RequestMapping("/api/profiles")
@RequiredArgsConstructor
public class ProfileController {
	
	private final ProfileService profileService;
	
	@PostMapping
	@Loggable
	public ProfileDTO saveProfile(@RequestBody @Valid CreateProfileRequest createProfileRequest) {
		return profileService.saveProfile(createProfileRequest);
	}
	
	@GetMapping("/{sessionId}")
	@Loggable
	public ProfileDTO getProfile(@PathVariable("sessionId") String sessionId) {
		return profileService.getProfile(UUID.fromString(sessionId));
	}
	
}
