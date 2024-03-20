package com.helmes.recruitment.formhandler.mapper;

import com.helmes.recruitment.formhandler.domain.Sector;
import com.helmes.recruitment.formhandler.models.SectorDTO;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class SectorMapperTest {
	
	@Test
	void toDTOs_shouldMapSectorsToSectorDTOs() {
		Sector manufacturing = createSector(1L, "Manufacturing");
		Sector constructionMaterials = createSector(2L, "Construction materials");
		Sector foodAndBeverage = createSector(3L, "Food and Beverage");
		Sector bakeryAndConfectioneryProducts = createSector(4L, "Bakery & confectionery products");
		manufacturing.setChildren(Set.of(constructionMaterials, foodAndBeverage));
		constructionMaterials.setChildren(Set.of(bakeryAndConfectioneryProducts));
		
		Set<SectorDTO> result = SectorMapper.toDTOs(List.of(manufacturing));
		
		assertThat(result)
				.hasSize(1)
				.flatExtracting(SectorDTO::getId, SectorDTO::getName, SectorDTO::getLevel)
				.containsExactly(1L, "Manufacturing", 0);
		
		SectorDTO manufacturingDTO = result.iterator().next();
		assertThat(manufacturingDTO.getChildren())
				.hasSize(2)
				.flatExtracting(SectorDTO::getId, SectorDTO::getName, SectorDTO::getLevel)
				.containsExactly(
						2L, "Construction materials", 1,
						3L, "Food and Beverage", 1
				);
		
		assertThat(manufacturingDTO.getChildren().iterator().next().getChildren())
				.hasSize(1)
				.flatExtracting(SectorDTO::getId, SectorDTO::getName, SectorDTO::getLevel)
				.containsExactly(
						4L, "Bakery & confectionery products", 2
				);
	}
	
	private Sector createSector(Long id, String name) {
		Sector sector = new Sector();
		sector.setId(id);
		sector.setName(name);
		return sector;
	}
}
