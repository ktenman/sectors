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
import static org.assertj.core.api.Assertions.tuple;
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
		sector1.setChildren(List.of(sector2, sector3));
		sector3.setChildren(List.of(sector4));
		
		when(sectorRepository.findAll()).thenReturn(List.of(sector1, sector1, sector2, sector3, sector4));
		
		Set<SectorDTO> result = sectorService.getAllSectors();
		
		assertThat(result).isNotEmpty()
				.extracting(SectorDTO::getId, SectorDTO::getName, SectorDTO::getLevel)
				.containsExactly(
						tuple(1L, "Manufacturing", 0),
						tuple(2L, "Construction materials", 1),
						tuple(3L, "Food and Beverage", 1),
						tuple(4L, "Bakery & confectionery products", 2)
				);
	}
	
	private Sector sector(Long id, String name) {
		Sector sector = new Sector();
		sector.setId(id);
		sector.setName(name);
		return sector;
	}
}
