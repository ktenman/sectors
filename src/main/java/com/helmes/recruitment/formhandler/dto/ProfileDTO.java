package com.helmes.recruitment.formhandler.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.UUID;

@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProfileDTO {
	private Long id;
	private String name;
	private Boolean agreeToTerms;
	private UUID sessionId;
	private List<Long> sectors;
}
