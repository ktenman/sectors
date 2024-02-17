package com.helmes.recruitment.formhandler.service;

import com.helmes.recruitment.formhandler.domain.Profile;
import com.helmes.recruitment.formhandler.domain.Sector;
import com.helmes.recruitment.formhandler.models.CreateProfileRequest;
import com.helmes.recruitment.formhandler.models.ProfileDTO;
import com.helmes.recruitment.formhandler.repository.ProfileRepository;
import com.helmes.recruitment.formhandler.service.lock.LockSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileService {
	
	private final ProfileRepository profileRepository;
	private final SectorService sectorService;
	private final SessionService sessionService;
	
	@LockSession
	public ProfileDTO saveProfile(CreateProfileRequest createProfileRequest) {
		UUID sessionId = sessionService.getSession();
		Set<Sector> sectors = sectorService.findSectorsByIds(createProfileRequest.getSectors());
		Profile profile = profileRepository.findBySessionId(sessionId)
				.map(existingProfile -> {
							existingProfile.setName(createProfileRequest.getName());
							existingProfile.setAgreeToTerms(createProfileRequest.getAgreeToTerms());
							existingProfile.setSectors(sectors);
							return existingProfile;
						}
				).orElse(Profile.builder()
						.name(createProfileRequest.getName())
						.agreeToTerms(createProfileRequest.getAgreeToTerms())
						.sectors(sectors)
						.sessionId(sessionId)
						.build()
				);
		
		Profile savedProfile = profileRepository.save(profile);
		return new ProfileDTO(
				savedProfile.getId(),
				savedProfile.getName(),
				savedProfile.getAgreeToTerms(),
				sectors.stream().map(Sector::getId).toList()
		);
	}
}
