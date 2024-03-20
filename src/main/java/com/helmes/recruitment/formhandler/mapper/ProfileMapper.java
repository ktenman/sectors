package com.helmes.recruitment.formhandler.mapper;

import com.helmes.recruitment.formhandler.domain.Profile;
import com.helmes.recruitment.formhandler.domain.Sector;
import com.helmes.recruitment.formhandler.models.ProfileDTO;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProfileMapper {
	
	public static ProfileDTO toDTO(Profile profile) {
		if (profile == null) {
			return null;
		}
		
		return new ProfileDTO(
				profile.getId(),
				profile.getName(),
				profile.getAgreeToTerms(),
				profile.getSectors().stream().map(Sector::getId).toList()
		);
	}
}
