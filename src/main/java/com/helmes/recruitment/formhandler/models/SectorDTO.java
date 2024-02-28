package com.helmes.recruitment.formhandler.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class SectorDTO implements Serializable, Comparable<SectorDTO> {
	
	private static final long serialVersionUID = -143526534123L;
	
	@EqualsAndHashCode.Include
	private Long id;
	private String name;
	private int level;
	private Set<SectorDTO> children = new HashSet<>();
	
	@Override
	public int compareTo(SectorDTO o) {
		return o.getName().compareTo(this.getName());
	}
}
