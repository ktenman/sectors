package com.helmes.recruitment.formhandler.controller;


import com.helmes.recruitment.formhandler.dto.CreateProfileRequest;
import com.helmes.recruitment.formhandler.dto.PofileDTO;
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
	public PofileDTO saveProfile(@RequestBody @Valid CreateProfileRequest createProfileRequest) {
		return profileService.saveProfile(createProfileRequest);
	}
}
