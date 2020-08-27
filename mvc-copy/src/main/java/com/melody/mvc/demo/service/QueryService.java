package com.melody.mvc.demo.service;

import com.melody.mvc.annocation.QHService;

/**
 * TODO
 * @author zqhuangc
 */
@QHService
public class QueryService implements IQueryService {

    @Override
    public String search(String name) {
        return "invork search name = " + name;
    }
}
