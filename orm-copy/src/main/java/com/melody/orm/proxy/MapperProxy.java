package com.melody.orm.proxy;


import com.melody.orm.config.MapperRegistry;
import com.melody.orm.session.QHSqlSession;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author zqhuangc
 */
public class MapperProxy<T> implements InvocationHandler {

    private QHSqlSession sqlSession;

    private Class<T> mapperInterface;

    public MapperProxy(QHSqlSession qhSqlSession, Class<T> clazz) {
        this.sqlSession = qhSqlSession;
        this.mapperInterface = clazz;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        MapperRegistry.MapperData mapperData =
                sqlSession.getConfiguration().getMapperRegistry().
                        get(method.getDeclaringClass().getName() + "." + method.getName());

        if(null != mapperData){
            System.out.println(String.format("SQL [ %s ], parameter [%s] ", mapperData.getSql(), args[0]));
            return sqlSession.selectOne(mapperData,String.valueOf(args[0]));
        }

        return method.invoke(proxy,args);
    }
}
