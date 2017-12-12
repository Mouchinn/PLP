package com.cg.uas.controller;

import org.apache.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;


@Aspect
public class LoggerAspect {

	Logger logger = Logger.getRootLogger();
	
	@AfterReturning("execution(public * com.cg.uas.controller.*.*(..))")
	public void myAdviceAfterReturning(JoinPoint jp){
		logger.info("after successfull execution this method called : "+jp.getSignature().getName());
	}
	
	@AfterThrowing(pointcut="execution(public * com.cg.uas.controller.*.*(..))",throwing="e")
	public void myAdviceAfterThrowing(JoinPoint jp,Throwable e) throws Throwable{
		logger.info("after getting exception this method called : "+jp.getSignature().getName());
		logger.error("error--"+e.getMessage());
		
	}
	
	@Around("execution(public * com.cg.uas.controller.*.*(..))")
	public Object myAdviceAround(ProceedingJoinPoint pj) throws Throwable
	{
		logger.info("before execution this method called : "+pj.getSignature().getName());
		Object result = pj.proceed();
		logger.info("after execution this method called : "+pj.getSignature().getName());
		return result;
		
	}
}
