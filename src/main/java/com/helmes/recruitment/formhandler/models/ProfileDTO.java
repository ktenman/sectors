package com.helmes.recruitment.formhandler.models;

import java.util.List;
import java.util.UUID;

public record ProfileDTO(Long id, String name, Boolean agreeToTerms, List<Long> sectors, UUID sessionId) {
}
