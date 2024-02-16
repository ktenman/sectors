package com.helmes.recruitment.formhandler.service;

import com.helmes.recruitment.formhandler.configuration.RedisConfiguration;
import com.helmes.recruitment.formhandler.domain.Sector;
import com.helmes.recruitment.formhandler.dto.SectorDTO;
import com.helmes.recruitment.formhandler.repository.SectorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SectorService {
	
	private final SectorRepository sectorRepository;
	
	@Cacheable(value = RedisConfiguration.ONE_DAY_CACHE, key = "'sectors'")
	public List<SectorDTO> getAllSectorsWithChildren() {
		List<Sector> sectors = sectorRepository.findAll();
		return sectors.stream()
				.map(this::mapSectorToDTO)
				.toList();
	}
	
	private SectorDTO mapSectorToDTO(Sector sector) {
		List<SectorDTO> children = sector.getChildren().stream()
				.map(this::mapSectorToDTO)
				.toList();
		return SectorDTO.builder()
				.id(sector.getId())
				.name(sector.getName())
				.children(children)
				.build();
	}
	
}
