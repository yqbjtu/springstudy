package com.yq.controller;

import com.alibaba.fastjson.JSONObject;
import com.yq.service.MsgProcessor;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

/**
 * Simple to Introduction
 * className: ProducerController

 */
@RestController
@RequestMapping(value = "/")
public class RedisController {

    @Autowired
    MsgProcessor msgProcessor;

    private static final Logger logger = LoggerFactory.getLogger(RedisController.class);

    @ApiOperation(value = "start multi thread test")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "count", value = "5", required = true, dataType = "int", paramType = "query"),
    })
    @GetMapping(value = "/start", produces = "application/json;charset=UTF-8")
    public String start(@RequestParam int count) {
        logger.info("enter start, count={}", count);

        msgProcessor.createRunnable(count);
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("currentTime", LocalDateTime.now().toString());
        return jsonObj.toJSONString();
    }

}
