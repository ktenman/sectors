package com.helmes.recruitment.formhandler.controller;

import com.helmes.recruitment.formhandler.configuration.logging.aspect.Loggable;
import com.helmes.recruitment.formhandler.dto.SectorDTO;
import com.helmes.recruitment.formhandler.service.SectorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/sectors")
@RequiredArgsConstructor
public class SectorController {
	
	private final SectorService sectorService;
	
	@GetMapping
	@Loggable
	public List<SectorDTO> getAllSectors() {
		return sectorService.getAllSectorsWithChildren();
	}
}
