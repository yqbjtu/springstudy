package com.yq.aop.aop;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.springframework.aop.MethodBeforeAdvice;


public class DemoMethodBeforeAdvice implements MethodBeforeAdvice {

    @Override
    public void before(Method method, Object[] args, Object target)
    throws Throwable {
        System.out.println("目标方法:" + method.getName() + "执行之前执行， with " + args.length + "个参数:"+ Arrays.toString(args));
    }
}

