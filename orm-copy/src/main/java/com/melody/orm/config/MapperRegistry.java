package com.melody.orm.config;

import com.melody.orm.domain.entity.Test;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


/**
 * @author zqhuangc
 */
public class MapperRegistry {

    private final ConcurrentMap<String,MapperData> methodSqlMapping = new ConcurrentHashMap<>();

    // 使用
    // 1. 在这里配置
    // 2. Java Bean的属性名字要和数据库表的名字一致

    public MapperRegistry() {
        methodSqlMapping.putIfAbsent("com.melody.orm.mapper.TestMapper.selectByPrimaryKey",
                new MapperData("select * from test where id = %d",Test.class));
    }

    public MapperData get(String nameSpace){
        return methodSqlMapping.get(nameSpace);
    }

    public class MapperData<T>{
        private String sql;
        private Class<T> type;

        public MapperData(String sql, Class<T> type) {
            this.sql = sql;
            this.type = type;
        }

        public String getSql() {
            return sql;
        }

        public void setSql(String sql) {
            this.sql = sql;
        }

        public Class<T> getType() {
            return type;
        }

        public void setType(Class<T> type) {
            this.type = type;
        }
    }

}
