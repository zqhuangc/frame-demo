package com.melody.opensource.springbootelkdemo.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.melody.opensource.springbootelkdemo.SpringBootElkDemoApplication;
import com.melody.opensource.springbootelkdemo.annotation.AopLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

/**
 * @description
 *
 * @author zqhuangc
 */
@RestController
@RequestMapping("/api")
public class DemoController {

    private static Logger logger = LoggerFactory.getLogger(SpringBootElkDemoApplication.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    @AopLog
    @GetMapping("/test")
    public String test() throws Exception {
        logger.debug("invoke test debug!");
        logger.info("invoke test info!");
        logger.warn("invoke test warn!");
        return "operation success";
    }

    @AopLog
    @GetMapping("/query")
    public String query(@RequestParam(required = false, value = "id")Integer id) throws Exception {
        return "query success" + id;
    }

    @AopLog
    @PostMapping("/login")
    public String login(@RequestBody JsonNode jsonNode) throws Exception {
        JsonNode username = jsonNode.get("username");
        return "login success" + username;
    }

    @AopLog
    @GetMapping("/error")
    public void error() throws Exception {
        throw new Exception("operation occur exception"+ System.currentTimeMillis());
    }

}
