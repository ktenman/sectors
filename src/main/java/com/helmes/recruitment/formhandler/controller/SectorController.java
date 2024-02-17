package com.helmes.recruitment.formhandler.controller;

import com.helmes.recruitment.formhandler.configuration.logging.aspect.Loggable;
import com.helmes.recruitment.formhandler.models.SectorDTO;
import com.helmes.recruitment.formhandler.service.SectorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/api/sectors")
@RequiredArgsConstructor
@Slf4j
public class SectorController {
	
	private final SectorService sectorService;
	
	@GetMapping
	@Loggable
	public Set<SectorDTO> getAllSectors() {
		return sectorService.getAllSectors();
	}
}
