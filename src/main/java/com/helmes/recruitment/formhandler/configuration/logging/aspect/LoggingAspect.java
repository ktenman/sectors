package com.helmes.recruitment.formhandler.configuration.logging.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.UUID;

import static com.helmes.recruitment.formhandler.configuration.TimeUtility.durationInSeconds;

@Aspect
@Component
@Slf4j
public class LoggingAspect {
	
	private static final String TRANSACTION_ID = "transactionId";
	@Resource
	private ObjectMapper objectMapper;
	
	private static void setTransactionId() {
		UUID uuid = UUID.randomUUID();
		MDC.put(TRANSACTION_ID, String.format("[%s] ", uuid));
	}
	
	private static void clearTransactionId() {
		MDC.remove(TRANSACTION_ID);
	}
	
	@Around("@annotation(Loggable)")
	public Object logMethod(ProceedingJoinPoint joinPoint) throws Throwable {
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		Method method = signature.getMethod();
		
		if (method.getReturnType().equals(Void.TYPE)) {
			log.warn("Loggable annotation should not be used on methods without return type: {}", method.getName());
			return joinPoint.proceed();
		}
		
		return logInvocation(joinPoint);
	}
	
	private Object logInvocation(ProceedingJoinPoint joinPoint) throws Throwable {
		long startTime = System.nanoTime();
		setTransactionId();
		try {
			logEntry(joinPoint);
			Object result = joinPoint.proceed();
			logExit(joinPoint, result, startTime);
			return result;
		} catch (Throwable throwable) {
			log.error("Exception in method: {}", joinPoint.getSignature().toShortString(), throwable);
			throw throwable;
		} finally {
			clearTransactionId();
		}
	}
	
	private void logEntry(ProceedingJoinPoint joinPoint) throws Throwable {
		String argsJson = objectMapper.writeValueAsString(joinPoint.getArgs());
		log.info("{} entered with arguments: {}", joinPoint.getSignature().toShortString(), argsJson);
	}
	
	private void logExit(ProceedingJoinPoint joinPoint, Object result, long startTime) throws Throwable {
		String resultJson = objectMapper.writeValueAsString(result);
		log.info("{} exited with result: {} in {} seconds", joinPoint.getSignature().toShortString(), resultJson, durationInSeconds(startTime));
	}
}
