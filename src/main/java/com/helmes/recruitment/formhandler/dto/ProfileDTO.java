package com.helmes.recruitment.formhandler.dto;

import java.util.List;

public record ProfileDTO(Long id, String name, Boolean agreeToTerms, List<Long> sectors) {
}
