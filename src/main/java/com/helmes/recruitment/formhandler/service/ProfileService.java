package com.helmes.recruitment.formhandler.service;

import com.helmes.recruitment.formhandler.configuration.exception.AccessDeniedException;
import com.helmes.recruitment.formhandler.domain.Profile;
import com.helmes.recruitment.formhandler.domain.Sector;
import com.helmes.recruitment.formhandler.models.CreateProfileRequest;
import com.helmes.recruitment.formhandler.models.ProfileDTO;
import com.helmes.recruitment.formhandler.models.ServiceResult;
import com.helmes.recruitment.formhandler.models.ServiceResult.ServiceOutcome;
import com.helmes.recruitment.formhandler.repository.ProfileRepository;
import com.helmes.recruitment.formhandler.service.lock.LockSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileService {
	
	private static final String PROFILE_NOT_FOUND_MESSAGE = "Profile not found for sessionId: %s";
	private final ProfileRepository profileRepository;
	private final SectorService sectorService;
	private final SessionService sessionService;
	
	@LockSession
	public ServiceResult<ProfileDTO> saveProfile(CreateProfileRequest createProfileRequest) {
		Set<Sector> sectors = sectorService.findSectorsByIds(createProfileRequest.getSectors());
		UUID sessionId = sessionService.getSession();
		
		Optional<Profile> existingProfile = profileRepository.findBySessionId(sessionId);
		Profile profile = existingProfile.orElseGet(Profile::new);
		profile.setName(createProfileRequest.getName());
		profile.setAgreeToTerms(createProfileRequest.getAgreeToTerms());
		profile.setSectors(sectors);
		profile.setSessionId(sessionId);
		
		Profile savedProfile = profileRepository.save(profile);
		
		return new ServiceResult<>(
				mapProfileToDTO(savedProfile),
				existingProfile.isPresent() ? ServiceOutcome.UPDATED : ServiceOutcome.CREATED
		);
	}
	
	public ProfileDTO getProfile() {
		UUID sessionId = sessionService.getSession();
		return profileRepository.findBySessionId(sessionId)
				.map(this::mapProfileToDTO)
				.orElseThrow(() -> new AccessDeniedException(String.format(PROFILE_NOT_FOUND_MESSAGE, sessionId)));
	}
	
	private ProfileDTO mapProfileToDTO(Profile profile) {
		return new ProfileDTO(
				profile.getId(),
				profile.getName(),
				profile.getAgreeToTerms(),
				profile.getSectors().stream().map(Sector::getId).toList()
		);
	}
	
}
