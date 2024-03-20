package com.helmes.recruitment.formhandler.models;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Schema(description = "Data Transfer Object for representing a sector including its sub-sectors")
public class SectorDTO implements Comparable<SectorDTO> {
	@EqualsAndHashCode.Include
	@Schema(description = "The unique identifier of the sector", example = "10")
	private Long id;
	
	@Schema(description = "Name of the sector", example = "Technology")
	private String name;
	
	@Schema(description = "Level of the sector in the hierarchy", example = "1")
	private int level;
	
	@ArraySchema(arraySchema = @Schema(description = "Set of child sectors under this sector. " +
			"Each child sector includes an id, name, level, and a set of its own children.",
			example = "[{'id': 1, 'name': 'Technology', 'level': 2, 'children': []}]"),
			minItems = 0,
			uniqueItems = true)
	private Set<SectorDTO> children = new HashSet<>();
	
	@Override
	public int compareTo(SectorDTO o) {
		return o.getName().compareTo(this.getName());
	}
}
