package com.helmes.recruitment.formhandler.service.lock;

import com.helmes.recruitment.formhandler.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class LockSessionAspect {
	
	private final SessionService sessionService;
	private final LockService lockService;
	
	@Around("@annotation(LockSession)")
	public Object aroundLockedSessionMethod(ProceedingJoinPoint joinPoint) throws Throwable {
		String sessionId = sessionService.getSession().toString();
		try {
			lockService.acquireLock(sessionId);
			return joinPoint.proceed();
		} finally {
			lockService.releaseLock(sessionId);
		}
	}
}

