package com.helmes.recruitment.formhandler.repository;

import com.helmes.recruitment.formhandler.domain.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
}
