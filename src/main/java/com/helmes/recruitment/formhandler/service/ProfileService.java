package com.helmes.recruitment.formhandler.service;

import com.helmes.recruitment.formhandler.configuration.exception.AccessDeniedException;
import com.helmes.recruitment.formhandler.domain.Profile;
import com.helmes.recruitment.formhandler.domain.Sector;
import com.helmes.recruitment.formhandler.models.CreateProfileRequest;
import com.helmes.recruitment.formhandler.models.ProfileDTO;
import com.helmes.recruitment.formhandler.models.ServiceResult;
import com.helmes.recruitment.formhandler.repository.ProfileRepository;
import com.helmes.recruitment.formhandler.service.lock.LockSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class ProfileService {
	
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
		
		ProfileDTO profileDTO = toProfileDTO(savedProfile);
		HttpStatus httpStatus = existingProfile.isPresent() ? HttpStatus.OK : HttpStatus.CREATED;
		
		return new ServiceResult<>(profileDTO, httpStatus);
	}
	
	public ProfileDTO getProfile() {
		UUID sessionId = sessionService.getSession();
		Function<Profile, ProfileDTO> profileToDTO = p -> new ProfileDTO(
				p.getId(),
				p.getName(),
				p.getAgreeToTerms(),
				p.getSectors().stream().map(Sector::getId).toList()
		);
		return profileRepository.findBySessionId(sessionId)
				.map(profileToDTO)
				.orElseThrow(() -> new AccessDeniedException(String.format("Profile not found for sessionId: %s", sessionId)));
	}
	
	private ProfileDTO toProfileDTO(Profile profile) {
		return new ProfileDTO(
				profile.getId(),
				profile.getName(),
				profile.getAgreeToTerms(),
				profile.getSectors().stream().map(Sector::getId).toList()
		);
	}
	
}
