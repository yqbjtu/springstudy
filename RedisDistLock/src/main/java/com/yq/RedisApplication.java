package com.yq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ConfigurableApplicationContext;
import springfox.documentation.annotations.Cacheable;

/**
 * Simple to Introduction
 * className: RedisApplication
 *
 * @author EricYang
 * @version 2018/7/14 11:48
 */
@SpringBootApplication
@EnableCaching
public class RedisApplication {
    private static final Logger logger = LoggerFactory.getLogger(RedisApplication.class);
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(RedisApplication.class, args);
        logger.info("Done start Spring boot");
    }
}
