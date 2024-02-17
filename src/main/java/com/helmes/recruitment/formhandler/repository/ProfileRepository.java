package com.helmes.recruitment.formhandler.repository;

import com.helmes.recruitment.formhandler.domain.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
	Optional<Profile> findBySessionId(UUID sessionId);
}
