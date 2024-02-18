package com.helmes.recruitment.formhandler.models;

import org.springframework.http.HttpStatus;

public record ServiceResult<T>(T body, HttpStatus status) {
}
