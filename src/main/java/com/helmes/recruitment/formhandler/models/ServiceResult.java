package com.helmes.recruitment.formhandler.models;

public record ServiceResult<T>(T body, ServiceOutcome outcome) {
	public enum ServiceOutcome {
		CREATED,
		UPDATED,
	}
}

