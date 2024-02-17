package com.helmes.recruitment.formhandler.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreateProfileRequest {
	
	@NotBlank(message = "Name is required")
	@Size(max = 64, message = "Name must not exceed {max} characters")
	private String name;
	
	@NotEmpty(message = "At least one sector must be selected")
	private List<Long> sectors;
	
	@NotNull(message = "Agreement to terms is mandatory")
	private Boolean agreeToTerms;
	
	private UUID sessionId;
	
}
