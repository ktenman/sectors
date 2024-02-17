package com.helmes.recruitment.formhandler.service;

import com.helmes.recruitment.formhandler.domain.Profile;
import com.helmes.recruitment.formhandler.domain.Sector;
import com.helmes.recruitment.formhandler.dto.CreateProfileRequest;
import com.helmes.recruitment.formhandler.dto.PofileDTO;
import com.helmes.recruitment.formhandler.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class ProfileService {
	
	private final ProfileRepository profileRepository;
	private final SectorService sectorService;
	
	@Transactional
	public PofileDTO saveProfile(CreateProfileRequest createProfileRequest) {
		Set<Sector> sectors = sectorService.findSectorsByIds(createProfileRequest.getSectors());
		
		Profile profile = Profile.builder()
				.name(createProfileRequest.getName())
				.agreeToTerms(createProfileRequest.getAgreeToTerms())
				.sectors(sectors)
				.sessionId("dummySessionId")
				.build();
		
		Profile savedProfile = profileRepository.save(profile);
		
		return PofileDTO.builder()
				.id(savedProfile.getId())
				.name(savedProfile.getName())
				.agreeToTerms(savedProfile.getAgreeToTerms())
				.sessionId(savedProfile.getSessionId())
				.sectors(savedProfile.getSectors().stream().map(Sector::getId).toList())
				.build();
	}
	
}
