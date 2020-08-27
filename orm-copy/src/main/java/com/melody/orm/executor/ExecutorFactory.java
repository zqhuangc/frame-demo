package com.melody.orm.executor;

import com.melody.orm.config.QHConfiguration;

/**
 * @author zqhuangc
 */
public class ExecutorFactory {

    private static final String SIMPLE = ExecutorType.SIMPLE.name();
    private static final String CACHING = ExecutorType.CACHING.name();


    public static Executor DEFAULT(QHConfiguration configuration) {
        return get(SIMPLE, configuration);
    }

    public static Executor get(String key, QHConfiguration configuration) {
        if (SIMPLE.equalsIgnoreCase(key)) {
            return new SimpleExecutor(configuration);
        }
        if (CACHING.equalsIgnoreCase(key)) {
            return new CachingExecutor(new SimpleExecutor(configuration));
        }
        throw new RuntimeException("no executor found");
    }

    public enum ExecutorType {
        SIMPLE, CACHING
    }
}
