package com.melody.opensource.springbootelkdemo.controller;

import com.melody.opensource.springbootelkdemo.SpringBootElkDemoApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @description
 *
 * @author zqhuangc
 */
@RestController
@RequestMapping("/api")
public class DemoController {

    private static Logger logger = LoggerFactory.getLogger(SpringBootElkDemoApplication.class);

    @RequestMapping("/test")
    public void test() throws Exception {
        logger.debug("invoke test debug!" + System.currentTimeMillis());
        logger.info("invoke test info!" + System.currentTimeMillis());
        logger.warn("invoke test warn!" + System.currentTimeMillis());
        //throw new Exception("test() occur exception");
    }

    @RequestMapping("/error")
    public void error() throws Exception {
        throw new Exception("test() occur exception"+ System.currentTimeMillis());
    }

}
