package com.melody.orm;


import com.melody.orm.config.QHConfiguration;
import com.melody.orm.domain.entity.Test;
import com.melody.orm.executor.ExecutorFactory;
import com.melody.orm.mapper.TestMapper;
import com.melody.orm.session.QHSqlSession;

import java.io.IOException;

/**
 * @author zqhuangc
 */
public class Bootstrap {
    public static void main(String[] args) throws IOException {
        start();
    }

    private static void start() throws IOException {
        QHConfiguration configuration = new QHConfiguration();
        configuration.doScanPath("com.melody.orm.mapper");
        configuration.build();
        QHSqlSession sqlSession = new QHSqlSession(configuration,
                ExecutorFactory.get(ExecutorFactory.ExecutorType.CACHING.name(),configuration));
        TestMapper testMapper = sqlSession.getMapper(TestMapper.class);
        long start = System.currentTimeMillis();
        Test test = testMapper.selectByPrimaryKey(1);
        System.out.println("cost:"+ (System.currentTimeMillis() -start));
        System.out.println(test.toString());
    }
}
