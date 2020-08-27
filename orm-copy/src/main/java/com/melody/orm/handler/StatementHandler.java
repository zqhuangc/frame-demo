package com.melody.orm.handler;

import com.melody.orm.config.MapperRegistry;
import com.melody.orm.config.QHConfiguration;

import java.sql.*;

/**
 *
 * 数据库操作和结果处理分发
 * @author zqhuangc
 */
public class StatementHandler {

    private final QHConfiguration configuration;

    private final ResultSetHandler resultSetHandler;

    public StatementHandler(QHConfiguration configuration) {
        this.configuration = configuration;
        this.resultSetHandler = new ResultSetHandler(configuration);
    }

    public <E> E query(MapperRegistry.MapperData mapperData, Object parameter) throws Exception {
        // JDBC
        Connection connection = getConnection();
        //TODO ParameterHandler
        PreparedStatement preparedStatement = connection.prepareStatement(String.format(mapperData.getSql(),
                Integer.parseInt((String.valueOf(parameter)))));
        preparedStatement.execute();
        //ResultSetHandler
        return (E) resultSetHandler.handle(preparedStatement, mapperData);
    }

    public Connection getConnection(){
        String driver = "com.mysql.cj.jdbc.Driver";
        String url = "jdbc:mysql://localhost:3306/db_storage?useUnicode=true&characterEncoding=utf-8&useSSL=false&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
        String username = "root";
        String password = "123456";
        Connection conn = null;
        try {
            Class.forName(driver);//classLoader,加载对应驱动
            conn = DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }

}
