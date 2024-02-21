package com.helmes.recruitment.formhandler.service;

import com.helmes.recruitment.formhandler.domain.Sector;
import com.helmes.recruitment.formhandler.models.SectorDTO;
import com.helmes.recruitment.formhandler.repository.SectorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SectorServiceTest {
	
	@Mock
	private SectorRepository sectorRepository;
	
	@InjectMocks
	private SectorService sectorService;
	
	@Test
	void getAllSectors() {
		Sector sector1 = sector(1L, "Manufacturing");
		Sector sector2 = sector(2L, "Construction materials");
		Sector sector3 = sector(3L, "Food and Beverage");
		Sector sector4 = sector(4L, "Bakery & confectionery products");
		sector1.setChildren(Set.of(sector2, sector3));
		sector2.setChildren(Set.of(sector4));
		when(sectorRepository.findAllParentSectors()).thenReturn(List.of(sector1));
		
		Set<SectorDTO> result = sectorService.getAllSectors();
		
		assertThat(result).hasSize(1)
				.flatExtracting(SectorDTO::getId, SectorDTO::getName, SectorDTO::getLevel)
				.containsExactly(1L, "Manufacturing", 0);
		
		
		SectorDTO manufacturingDTO = result.iterator().next();
		assertThat(manufacturingDTO.getChildren()).hasSize(2)
				.flatExtracting(SectorDTO::getId, SectorDTO::getName, SectorDTO::getLevel)
				.containsExactly(
						2L, "Construction materials", 1,
						3L, "Food and Beverage", 1
				);
		
		assertThat(manufacturingDTO.getChildren().iterator().next().getChildren()).hasSize(1)
				.flatExtracting(SectorDTO::getId, SectorDTO::getName, SectorDTO::getLevel)
				.containsExactly(
						4L, "Bakery & confectionery products", 2
				);
	}
	
	private Sector sector(Long id, String name) {
		Sector sector = new Sector();
		sector.setId(id);
		sector.setName(name);
		return sector;
	}
}
