package com.helmes.recruitment.formhandler.service;

import com.helmes.recruitment.formhandler.domain.Sector;
import com.helmes.recruitment.formhandler.models.SectorDTO;
import com.helmes.recruitment.formhandler.repository.SectorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.helmes.recruitment.formhandler.configuration.RedisConfiguration.ONE_DAY_CACHE;

/**
 * Service class for handling sector-related operations.
 * Provides functionality for retrieving and processing sectors and their hierarchy.
 */
@Service
@RequiredArgsConstructor
public class SectorService {
	
	private final SectorRepository sectorRepository;
	
	/**
	 * Finds and returns sectors by their IDs.
	 *
	 * @param sectorIds A list of sector IDs to find.
	 * @return A Set of {@link Sector} objects corresponding to the provided IDs.
	 */
	public Set<Sector> findSectorsByIds(List<Long> sectorIds) {
		return new HashSet<>(sectorRepository.findAllById(sectorIds));
	}
	
	/**
	 * Retrieves all sectors and their hierarchy.
	 *
	 * @return A Set of {@link SectorDTO} objects representing the sectors and their hierarchy.
	 */
	@Cacheable(value = ONE_DAY_CACHE, key = "'sectors'")
	public List<Sector> getAllSectors() {
		return sectorRepository.findAllParentSectors();
	}
	
}
