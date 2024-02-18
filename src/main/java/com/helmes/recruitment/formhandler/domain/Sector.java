package com.helmes.recruitment.formhandler.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Setter
public class Sector extends BaseEntity {
	
	private String name;
	@OneToMany
	@JoinColumn(name = "parent_id")
	private List<Sector> children = new ArrayList<>();
	
}

