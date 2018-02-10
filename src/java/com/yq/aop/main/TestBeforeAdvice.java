package com.yq.aop.main;

import org.aopalliance.aop.Advice;
import org.springframework.aop.Advisor;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.framework.autoproxy.BeanNameAutoProxyCreator;
import org.springframework.aop.support.RegexpMethodPointcutAdvisor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.yq.aop.aop.DemoMethodBeforeAdvice;
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

        System.out.println("");
        Advised advised = (Advised) service;
        Advisor[] advisors = advised.getAdvisors();
        int oldAdvisorCount = advisors.length;
        for(int i=0;i < oldAdvisorCount; i++) {
            System.out.println(i + "th " + " advisor:" + advisors[i].getAdvice().toString());
            advised.removeAdvisor(i);
            service.methodAdd();
        }

        System.out.println("");
        ServiceImpl svcImpl = (ServiceImpl)ctx.getBean("service");
        System.out.println("通过ServiceImpl对象调用方法");
        svcImpl.methodAdd();
        svcImpl.methodSubtract();
        svcImpl.processPipe();


        System.out.println("\nbean没有关联advice，通过Advised动态添加了beforeAdvice示例");
        IService service2 = (IService)ctx.getBean("serviceProxy2");
        System.out.println("bean还未添加beforeAdvice，调用methodAdd()");
        service2.methodAdd();

        System.out.println("bean关联advice后，调用methodAdd()，methodSubtract()");
        Advice obj = null;
        try {
            obj = DemoMethodBeforeAdvice.class.newInstance();
            Advised advised2 = (Advised) service2;
            advised2.addAdvice(obj);
            service2.methodAdd();
            service2.methodSubtract();
        }
        catch (InstantiationException e) {
            e.printStackTrace();
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        //动态创建ProxyFactoryBean，然后setTarget，结果不行，还需要研究如何动态创建ProxyFactoryBean
        /*System.out.println("\nbean没有示例");
        ServiceImpl serviceImpl3= ctx.getBean(ServiceImpl.class);  
        serviceImpl3.methodAdd();

        BeanNameAutoProxyCreator beanNameAutoProxyCreator = new BeanNameAutoProxyCreator();  
        //设置要创建代理的那些Bean的名字  
        beanNameAutoProxyCreator.setBeanNames("service*");
        //设置拦截链名字(这些拦截器是有先后顺序的)  
        beanNameAutoProxyCreator.setInterceptorNames("beforeAdvisor");
        ServiceImpl serviceImpl4 = (ServiceImpl)ctx.getBean("service");  
        serviceImpl4.methodAdd();*/
    }

}
