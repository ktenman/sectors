package com.helmes.recruitment.formhandler.models;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Data Transfer Object for user profile information")
public record ProfileDTO(
		@Schema(description = "The unique identifier of the user profile", example = "1") Long id,
		@Schema(description = "Name of the user", example = "Jane Doe") String name,
		@Schema(description = "Indicates if the user agrees to the terms", example = "true") Boolean agreeToTerms,
		@Schema(description = "List of sector IDs the user is interested in", example = "[4, 5, 6]") List<Long> sectors) {
}
