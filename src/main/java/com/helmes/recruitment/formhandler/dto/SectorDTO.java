package com.helmes.recruitment.formhandler.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SectorDTO implements Serializable {
	
	private static final long serialVersionUID = -143526534123L;
	
	private Long id;
	private String name;
	private List<SectorDTO> children;
	
}
