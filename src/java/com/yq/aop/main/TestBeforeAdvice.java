package com.yq.aop.main;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.yq.aop.service.IService;
import com.yq.aop.service.ServiceImpl;

public class TestBeforeAdvice {

    public static void main(String[] args) {
        String current = System.getProperty("user.dir");
        System.out.println("current path:" + current);
        //applicationContext.xml和com处于同级目录
        ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
        IService service = (IService)ctx.getBean("serviceProxy");
        service.methodAdd();
        //无法通过service调用processPipe()方法， 因为processPipe()方法不是IService接口的方法
        service.methodSubtract();

        ServiceImpl svcImpl = (ServiceImpl)ctx.getBean("service");
        System.out.println("通过ServiceImpl对象调用方法");
        svcImpl.methodAdd();
        svcImpl.methodSubtract();
        svcImpl.processPipe();
    }

}
