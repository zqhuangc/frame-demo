package com.melody.orm.executor;


import com.melody.orm.config.MapperRegistry;

/**
 * @author zqhuangc
 */
public interface Executor {
    <T> T query(MapperRegistry.MapperData mapperData, Object parameter) throws Exception;
}
