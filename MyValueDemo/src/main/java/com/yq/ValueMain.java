package com.yq;

import com.yq.domain.Bonus;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


public class ValueMain {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();

        ctx.register(com.yq.config.AppConfig.class);
        ctx.refresh();

        Bonus bonus= (Bonus)ctx.getBean("bonus");
        System.out.println(bonus.getName());
        System.out.println(bonus.getTime());
        System.out.println(bonus.getCount());
    }

}
