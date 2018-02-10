package com.yq.aop.service;

import org.aopalliance.aop.Advice;
import org.springframework.aop.Advisor;
import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.framework.AopConfigException;

public class ServiceImpl implements IService{

    @Override
    public void methodAdd() {
        System.out.println("方法 methodAdd");
    }

    public void processPipe() {
        System.out.println("方法 processPipe");
    }

    @Override
    public void methodSubtract() {
        System.out.println("方法 methodSubtract");
    }

}

