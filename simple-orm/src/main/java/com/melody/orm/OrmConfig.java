package com.melody.orm;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import javax.core.common.jdbc.datasource.DataSourceKey;
import javax.core.common.jdbc.datasource.DynamicDataSource;
import javax.core.common.jdbc.datasource.DynamicDataSourceHolder;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;


/**
 * TODO
 *
 * @author zqhuangc
 */
@EnableAspectJAutoProxy
@ComponentScan("com.melody.orm")
@Configuration
public class OrmConfig {

//    @Bean
//    public PropertySourcesPlaceholderConfigurer configurer(){
//        PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
//        configurer.setLocations(new ClassPathResource("classpath:local/db.properties"));
//        return configurer;
//    }

    @Bean("druidDataSource")
    public DataSource druidDataSource() throws SQLException {
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setUsername("root");
        druidDataSource.setPassword("123456");
        druidDataSource.setUrl("jdbc:mysql://127.0.0.1:3306/db_storage?useSSL=false&serverTimezone=UTC");
        druidDataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");

        druidDataSource.setInitialSize(1);
        druidDataSource.setMinIdle(1);
        druidDataSource.setMaxActive(200);
        druidDataSource.setMaxWait(60000);
        druidDataSource.setTimeBetweenEvictionRunsMillis(60000);
        druidDataSource.setMinEvictableIdleTimeMillis(300000);
        druidDataSource.setValidationQuery("SELECT 'x'");
        druidDataSource.setTestWhileIdle(true);
        druidDataSource.setTestOnBorrow(false);
        druidDataSource.setTestOnReturn(false);
        druidDataSource.setPoolPreparedStatements(false);
        druidDataSource.setMaxPoolPreparedStatementPerConnectionSize(20);
        druidDataSource.setFilters("stat,wall");

        return druidDataSource;
    }

    @Bean("dynamicDataSource")
    public DataSource dynamicDataSource(@Qualifier("druidDataSource")DataSource dataSource) {
        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        Map<Object, Object> dataSourceMap = new HashMap<>();
        dataSourceMap.put(DataSourceKey.READ.name(), dataSource );
        dataSourceMap.put(DataSourceKey.WRITE.name(), dataSource );
        dynamicDataSource.setDefaultTargetDataSource(dataSource);
        dynamicDataSource.setTargetDataSources(dataSourceMap);

        DynamicDataSourceHolder.getDataSourceKeys().addAll(dataSourceMap.keySet());

        return dynamicDataSource;
    }
}
