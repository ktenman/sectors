package com.helmes.recruitment.formhandler.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PofileDTO {
	private Long id;
	private String name;
	private Boolean agreeToTerms;
	private String sessionId;
	private List<Long> sectors;
}
