package com.helmes.recruitment.formhandler.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SessionService {
	
	private final HttpServletRequest request;
	private final StringRedisTemplate redisTemplate;
	
	public UUID getSession() {
		String sessionId = request.getSession(true).getId();
		return UUID.fromString(sessionId);
	}
	
}
