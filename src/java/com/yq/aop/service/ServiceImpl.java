package com.yq.aop.service;

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

