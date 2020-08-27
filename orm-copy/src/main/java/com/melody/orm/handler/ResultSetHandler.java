package com.melody.orm.handler;


import com.melody.orm.config.MapperRegistry;
import com.melody.orm.config.QHConfiguration;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author zqhuangc
 */
public class ResultSetHandler {

    private final QHConfiguration configuration;

    public ResultSetHandler(QHConfiguration configuration) {
        this.configuration = configuration;
    }

    public <E> E handle(PreparedStatement pstmt, MapperRegistry.MapperData mapperData) throws Exception{
        Object resultObj = mapperData.getType().getConstructor().newInstance();
        ResultSet resultSet = pstmt.getResultSet();

        if (resultSet.next()){
            int i = 0;
            for (Field field:resultObj.getClass().getDeclaredFields()){
                setValue(resultObj,field,resultSet,i);
            }
        }

        return (E) resultObj;

    }

    private void setValue(Object resultObj, Field field, ResultSet rs, int i) throws NoSuchMethodException, SQLException, InvocationTargetException, IllegalAccessException {
        Method setMethod = resultObj.getClass().getMethod("set" + upperCapital(field.getName()), field.getType());
        field.setAccessible(true);
        setMethod.invoke(resultObj, getResult(field,rs));
    }

    private Object getResult(Field field, ResultSet rs) throws SQLException {
        //TODO type handles
        Class<?> type = field.getType();
        if(Integer.class == type){
            return rs.getInt(field.getName());
        }else if(String.class == type){
            return rs.getString(field.getName());
        }
        return rs.getString(field.getName());
    }

    private String upperCapital(String name) {
        String first = name.substring(0, 1);
        String tail = name.substring(1);
        return first.toUpperCase() + tail;
    }
}
