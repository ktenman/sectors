package com.helmes.recruitment.formhandler.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "CreateProfileRequest model for creating a new user profile")
public class CreateProfileRequest {
	
	@NotBlank(message = "Name is required")
	@Size(max = 64, message = "Name must not exceed {max} characters")
	@Schema(description = "Name of the user", example = "John Doe")
	private String name;
	
	@NotEmpty(message = "At least one sector must be selected")
	@Schema(description = "List of sector IDs the user is interested in", example = "[1, 2, 3]")
	private List<Long> sectors;
	
	@AssertTrue(message = "Agreement to terms must be true")
	@NotNull(message = "Agreement to terms is mandatory")
	@Schema(description = "Whether the user agrees to the terms", example = "true")
	private Boolean agreeToTerms;
	
}
