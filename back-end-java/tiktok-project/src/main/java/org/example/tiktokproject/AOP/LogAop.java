package org.example.tiktokproject.AOP;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class LogAop {

    @Pointcut("@annotation(org.example.tiktokproject.AOP.MyLog)")
    public void pointcut() {

    }

    @Around("pointcut()")
    public Object MyLog(ProceedingJoinPoint joinPoint) throws Throwable {
        String name = joinPoint.getSignature().getName();
        log.info(name);
        return joinPoint.proceed();
    }
}
