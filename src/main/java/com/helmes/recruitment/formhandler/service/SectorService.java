package com.helmes.recruitment.formhandler.service;

import com.helmes.recruitment.formhandler.configuration.RedisConfiguration;
import com.helmes.recruitment.formhandler.domain.Sector;
import com.helmes.recruitment.formhandler.models.SectorDTO;
import com.helmes.recruitment.formhandler.repository.SectorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
	 * Retrieves all sectors along with their hierarchical structure and caches the result for one day.
	 * Each sector is transformed into a {@link SectorDTO} that includes child sectors, forming a tree structure.
	 *
	 * @return a Set of {@link SectorDTO} representing the hierarchical structure of sectors.
	 */
	@Cacheable(value = RedisConfiguration.ONE_DAY_CACHE, key = "'sectors'")
	public Set<SectorDTO> getAllSectors() {
		List<Sector> parentSectors = sectorRepository.findAllParentSectors();
		return parentSectors.stream()
				.map(sector -> mapSectorToDTO(sector, 0))
				.collect(Collectors.toSet());
	}
	
	private SectorDTO mapSectorToDTO(Sector sector, int currentLevel) {
		Set<SectorDTO> childDTOs = sector.getChildren().isEmpty() ? Collections.emptySet() :
				sector.getChildren().stream()
						.map(childSector -> mapSectorToDTO(childSector, currentLevel + 1))
						.collect(Collectors.toSet());
		
		return SectorDTO.builder()
				.id(sector.getId())
				.name(sector.getName())
				.level(currentLevel)
				.children(childDTOs)
				.build();
	}
	
}
