package com.sheltersdog.core.log

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.stereotype.Component

@Aspect
@Component
class LoggerAop {

    @Around("execution(* com.sheltersdog..*Controller.*(..)) " +
            "|| execution(* com.sheltersdog..*Service.*(..)) " +
            "|| execution(* com.sheltersdog..*PersistenceAdaptor.*(..)) " +
            "|| execution(* com.sheltersdog..*Repository.*(..))")
    fun traceLog(joinPoint: ProceedingJoinPoint): Any {
        saveMdcTrace("${joinPoint.signature.declaringTypeName}::${joinPoint.signature.name}")
        return joinPoint.proceed()
    }



}