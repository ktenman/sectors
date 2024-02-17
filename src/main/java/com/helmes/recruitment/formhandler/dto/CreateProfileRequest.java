package com.helmes.recruitment.formhandler.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreateProfileRequest {
	
	@NotBlank(message = "Name is required")
	private String name;
	
	@NotEmpty(message = "At least one sector must be selected")
	private List<Long> sectors;
	
	@NotNull(message = "Agreement to terms is mandatory")
	private Boolean agreeToTerms;
	
}
