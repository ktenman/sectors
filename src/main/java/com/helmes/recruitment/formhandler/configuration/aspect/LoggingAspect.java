package com.helmes.recruitment.formhandler.configuration.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;
import java.util.UUID;

import static com.helmes.recruitment.formhandler.configuration.TimeUtility.durationInSeconds;

@Aspect
@Component
@Slf4j
public class LoggingAspect {
    
    @Resource
    private ObjectMapper objectMapper;
    
    private static final String TRANSACTION_ID = "transactionId";
    
    private static void setTransactionId(UUID uuid) {
        String transactionId = uuid.toString();
        MDC.put(TRANSACTION_ID, "[" + transactionId + "] ");
    }
    
    private static void clearTransactionId() {
        MDC.remove(TRANSACTION_ID);
    }
    
    @Around("@annotation(com.helmes.recruitment.formhandler.configuration.aspect.Loggable)")
    public Object logMethod(ProceedingJoinPoint joinPoint) {
        long startTime = System.nanoTime();
        
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        
        if (method.getReturnType().equals(Void.TYPE)) {
            log.error("Loggable annotation is used on a method with no return type: {}", method.getName());
            throw new IllegalStateException("Loggable annotation cannot be used on methods with no return type");
        }
        
        setTransactionId(UUID.randomUUID());
        Object result = null;
        try {
            String argsJson = objectMapper.writeValueAsString(joinPoint.getArgs());
            log.info("Entered method: {} with arguments: {}", joinPoint.getSignature().toShortString(), argsJson);
            result = joinPoint.proceed();
            String resultJson = objectMapper.writeValueAsString(result);
            log.info("Exited method: {} with result: {} in {} seconds", joinPoint.getSignature().toShortString(), resultJson, durationInSeconds(startTime));
            return result;
        } catch (Throwable throwable) {
            log.error("Exception in method: {}", joinPoint.getSignature().toShortString(), throwable);
            throw new RuntimeException(throwable);
        } finally {
            if (!(result instanceof Mono)) {
                clearTransactionId();
            }
        }
    }
}
