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
	
	public boolean isSessionValid(UUID sessionId) {
		String sessionKey = "spring:session:sessions:" + sessionId;
		Boolean exists = redisTemplate.hasKey(sessionKey);
		return Boolean.TRUE.equals(exists);
	}
	
}
