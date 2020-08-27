package com.melody.orm.session;

import com.melody.orm.config.MapperRegistry;
import com.melody.orm.config.QHConfiguration;
import com.melody.orm.executor.Executor;
import com.melody.orm.executor.ExecutorFactory;
import com.melody.orm.proxy.MapperProxy;

import java.lang.reflect.Proxy;

/**
 * @author zqhuangc
 */
public class QHSqlSession {

    private QHConfiguration configuration;

    private Executor executor;

    // 关联起来
    public QHSqlSession(QHConfiguration configuration) {
        this.configuration = configuration;
        this.executor = ExecutorFactory.DEFAULT(configuration);
    }
    public QHSqlSession(QHConfiguration configuration, Executor executor) {
        this.configuration = configuration;
        this.executor = executor;
    }

    public QHConfiguration getConfiguration() {
        return configuration;
    }

    public <T> T getMapper(Class<T> clazz){
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(),
                new Class[]{clazz},
                new MapperProxy(this,clazz));
    }

    public <T> T selectOne(MapperRegistry.MapperData mapperData, Object parameter) throws Exception {
        return executor.query(mapperData, parameter);
    }
}
