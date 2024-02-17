package com.helmes.recruitment.formhandler.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class SectorDTO implements Serializable {
	
	private static final long serialVersionUID = -143526534123L;
	
	@EqualsAndHashCode.Include
	private Long id;
	private String name;
	private int level;
	
}
