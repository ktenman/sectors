package com.helmes.recruitment.formhandler.mapper;

import com.helmes.recruitment.formhandler.domain.Sector;
import com.helmes.recruitment.formhandler.models.SectorDTO;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SectorMapper {
	
	public static Set<SectorDTO> toDTOs(Collection<Sector> sectors) {
		return sectors.stream()
				.sorted(Comparator.comparing(Sector::getName))
				.map(sector -> toDto(sector, 0))
				.collect(Collectors.toCollection(LinkedHashSet::new));
	}
	
	private static SectorDTO toDto(Sector sector, int currentLevel) {
		Set<SectorDTO> childDTOs = sector.getChildren().isEmpty() ? Collections.emptySet() :
				sector.getChildren().stream()
						.sorted(Comparator.comparing(Sector::getName))
						.map(childSector -> toDto(childSector, currentLevel + 1))
						.collect(Collectors.toCollection(LinkedHashSet::new));
		
		return SectorDTO.builder()
				.id(sector.getId())
				.name(sector.getName())
				.level(currentLevel)
				.children(childDTOs)
				.build();
	}
	
}
