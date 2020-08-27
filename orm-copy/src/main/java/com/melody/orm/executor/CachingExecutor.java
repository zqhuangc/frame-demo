package com.melody.orm.executor;


import com.melody.orm.config.MapperRegistry;
import com.melody.orm.config.QHConfiguration;
import com.melody.orm.handler.StatementHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zqhuangc
 */
public class CachingExecutor implements Executor {
    private QHConfiguration configuration;

    private SimpleExecutor delegate;

    private Map<String,Object> localCache = new HashMap();

    public CachingExecutor(SimpleExecutor delegate) {
        this.delegate = delegate;
    }

    public CachingExecutor(QHConfiguration configuration) {
        this.configuration = configuration;
    }

    public <E> E query(MapperRegistry.MapperData mapperData, Object parameter)
            throws Exception {
        //初始化 StatementHandler --> ParameterHandler --> ResultSetHandler
        StatementHandler handler = new StatementHandler(configuration);
        Object result = localCache.get(mapperData.getSql());
        if( null != result){
            System.out.println("缓存命中");
            return (E)result;
        }
        result =  (E) delegate.query(mapperData,parameter);
        localCache.put(mapperData.getSql(),result);
        return (E)result;
    }
}