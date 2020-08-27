package com.melody.orm.executor;

import com.melody.orm.config.MapperRegistry;
import com.melody.orm.config.QHConfiguration;
import com.melody.orm.handler.StatementHandler;

/**
 * @author zqhuangc
 */
public class SimpleExecutor implements Executor {
    private QHConfiguration configuration;

    public SimpleExecutor(QHConfiguration configuration) {
        this.configuration = configuration;
    }

    public QHConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(QHConfiguration configuration) {
        this.configuration = configuration;
    }

    public <E> E query(MapperRegistry.MapperData mapperData, Object parameter)
            throws Exception {
        //初始化StatementHandler --> ParameterHandler --> ResultSetHandler
        StatementHandler handler = new StatementHandler(configuration);
        return (E) handler.query(mapperData, parameter);
    }
}