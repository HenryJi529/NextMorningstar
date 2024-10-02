package com.morningstar.practice.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.fusesource.jansi.Ansi;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

@Aspect
@Component
public class PracticeAspect {

    // 定义切入点表达式
    @Pointcut("execution(* com.morningstar.practice.service.*.*(..))")
    public void serviceMethods() {
    }

    // 前置通知
    @Before("serviceMethods()")
    public void logBeforeMethod() {
        System.out.println(Ansi.ansi().fg(Ansi.Color.BLUE).a("方法即将执行...").reset());
    }

    // 后置通知
    @After("serviceMethods()")
    public void logAfterMethod() {
        System.out.println(Ansi.ansi().fg(Ansi.Color.BLUE).a("方法执行完成...").reset());
    }

    // 返回后通知
    @AfterReturning(pointcut = "serviceMethods()", returning = "result")
    public void logAfterReturning(Object result) {
        System.out.println(Ansi.ansi().fg(Ansi.Color.BLUE).a("方法返回结果: ").reset().toString() + result);
    }

    // 异常通知
    @AfterThrowing(pointcut = "serviceMethods()", throwing = "ex")
    public void logAfterThrowing(Exception ex) {
        System.out.println(Ansi.ansi().fg(Ansi.Color.BLUE).a("方法抛出异常: ").reset() + ex.getMessage());
    }

    @Around("serviceMethods()")
    public Object logParams(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println(Ansi.ansi().fg(Ansi.Color.BLUE).a("Around: [start]").reset());
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Parameter[] parameters = method.getParameters();
        Object[] args = joinPoint.getArgs();

        for (int i = 0; i < parameters.length; i++) {
            System.out.printf("参数名: %s, 参数值: %s%n",
                    parameters[i].getName(),
                    args[i]);
        }
        Object result = joinPoint.proceed();
        System.out.println(Ansi.ansi().fg(Ansi.Color.BLUE).a("Around: [end]").reset());
        return result;
    }
}
