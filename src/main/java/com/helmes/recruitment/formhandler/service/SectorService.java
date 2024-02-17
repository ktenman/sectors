package com.helmes.recruitment.formhandler.service;

import com.helmes.recruitment.formhandler.configuration.RedisConfiguration;
import com.helmes.recruitment.formhandler.domain.Sector;
import com.helmes.recruitment.formhandler.models.SectorDTO;
import com.helmes.recruitment.formhandler.repository.SectorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class SectorService {
	
	private final SectorRepository sectorRepository;
	
	/**
	 * Retrieves all sectors with their children in a hierarchical structure.
	 * Caches the result for one day.
	 *
	 * @return a set of SectorDTO representing the sector hierarchy.
	 */
	@Cacheable(value = RedisConfiguration.ONE_DAY_CACHE, key = "'sectors'")
	public Set<SectorDTO> getAllSectors() {
		return sectorRepository.findAll().stream()
				.flatMap(sector -> streamSectorWithChildren(sector, 0))
				.collect(Collectors.toCollection(LinkedHashSet::new));
	}
	
	private Stream<SectorDTO> streamSectorWithChildren(Sector sector, int level) {
		SectorDTO currentSectorDTO = new SectorDTO(sector.getId(), sector.getName(), level);
		return Stream.concat(
				Stream.of(currentSectorDTO),
				sector.getChildren().stream().flatMap(child -> streamSectorWithChildren(child, level + 1))
		);
	}
	
	/**
	 * Finds and returns a set of sectors by their IDs.
	 * @param sectorIds the list of sector IDs to find.
	 * @return a set of Sectors corresponding to the provided IDs.
	 */
	public Set<Sector> findSectorsByIds(List<Long> sectorIds) {
		return new HashSet<>(sectorRepository.findAllById(sectorIds));
	}
}
