package com.helmes.recruitment.formhandler;

import com.helmes.recruitment.formhandler.domain.Profile;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DBHelperService {
	
	private final EntityManager entityManager;
	
	@Transactional
	public void persist(Object entity) {
		entityManager.persist(entity);
		entityManager.flush();
	}
	
	public List<Profile> getProfiles() {
		return entityManager.createQuery(
				"SELECT p FROM Profile p JOIN FETCH p.sectors", Profile.class
		).getResultList();
	}
}
