package com.helmes.recruitment.formhandler.repository;

import com.helmes.recruitment.formhandler.domain.Sector;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SectorRepository extends JpaRepository<Sector, Long> {
	
	@Query(value = "SELECT * FROM sector WHERE parent_id IS NULL", nativeQuery = true)
	List<Sector> findAllParentSectors();
}
