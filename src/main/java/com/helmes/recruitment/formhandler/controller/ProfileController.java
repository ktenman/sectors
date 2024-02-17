package com.helmes.recruitment.formhandler.controller;


import com.helmes.recruitment.formhandler.configuration.aspect.Loggable;
import com.helmes.recruitment.formhandler.dto.CreateProfileRequest;
import com.helmes.recruitment.formhandler.dto.ProfileDTO;
import com.helmes.recruitment.formhandler.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {
	
	private final ProfileService profileService;
	
	@PostMapping
	@Loggable
	public ProfileDTO saveProfile(@RequestBody @Valid CreateProfileRequest createProfileRequest) {
		return profileService.saveProfile(createProfileRequest);
	}
}
