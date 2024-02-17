package com.helmes.recruitment.formhandler.service.lock;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.function.UnaryOperator;

import static java.util.concurrent.TimeUnit.MINUTES;

@Service
@RequiredArgsConstructor
public class LockService {
	
	private static final long LOCK_TIMEOUT = 1;
	private static final UnaryOperator<String> LOCK_KEY = identifier -> "lock:" + identifier;
	private final StringRedisTemplate redisTemplate;
	
	public void acquireLock(String identifier) {
		Boolean acquired = redisTemplate.opsForValue().setIfAbsent(LOCK_KEY.apply(identifier), "locked", LOCK_TIMEOUT, MINUTES);
		if (!Boolean.TRUE.equals(acquired)) {
			throw new IllegalStateException("Unable to acquire lock for identifier: " + identifier);
		}
	}
	
	public void releaseLock(String identifier) {
		redisTemplate.delete(LOCK_KEY.apply(identifier));
	}
	
}
