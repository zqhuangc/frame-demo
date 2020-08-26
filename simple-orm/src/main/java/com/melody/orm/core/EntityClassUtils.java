package com.melody.orm.core;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @author zqhuangc
 */
public class EntityClassUtils {
    private EntityClassUtils(){}

    static final Set<Class<?>> SUPPORT_SQL_TYPE = new HashSet<Class<?>>();

    static{
        Class<?>[] classes = {
                short.class,Short.class,
                int.class,Integer.class,
                float.class,Float.class,
                long.class,Long.class,
                double.class,Double.class,
                boolean.class,Boolean.class,
                String.class,
                Date.class,
                Timestamp.class,
                BigDecimal.class,
                LocalDateTime.class,
                BigInteger.class
        };
        SUPPORT_SQL_TYPE.addAll(Arrays.asList(classes));
    }

    static boolean isSupportSQLType(Class<?> clazz){
        return clazz.isEnum() || SUPPORT_SQL_TYPE.contains(clazz);
    }

    public static Field[] findFields(Class<?> clazz){
        return clazz.getDeclaredFields();
    }

    public static Map<String, Method> findPublicGetters(Class<?> clazz){
        Map<String, Method> map = new HashMap<String, Method>();
        Method[] methods = clazz.getMethods();
        for (Method method: methods) {
            if(Modifier.isStatic(method.getModifiers())){
                continue;
            }
            if(method.getParameterTypes().length != 0){
                continue;
            }
            if(method.getName().equals("getClass")){
                continue;
            }

            Class<?> returnType = method.getReturnType();
            if(void.class.equals(returnType)){
                continue;
            }
            if(!isSupportSQLType(returnType)){
                continue;
            }
            if ((returnType.equals(boolean.class)
                    || returnType.equals(Boolean.class))
                    && method.getName().startsWith("is")
                    && method.getName().length() > 2) {
                map.put(getGetterName(method), method);
                continue;
            }
            if(!method.getName().startsWith("get")){
                continue;
            }
            if(method.getName().length()<4){
                continue;
            }
            map.put(getGetterName(method),method);
        }
        return map;

    }

    public static Map<String, Method> findPublicSetters(Class<?> clazz) {
        Map<String, Method> map = new HashMap<String, Method>();
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            if (Modifier.isStatic(method.getModifiers()))
                continue;
            if ( ! void.class.equals(method.getReturnType()))
                continue;
            if (method.getParameterTypes().length != 1)
                continue;
            if ( ! method.getName().startsWith("set"))
                continue;
            if (method.getName().length() < 4)
                continue;
            if(!isSupportSQLType(method.getParameterTypes()[0])){
                continue;
            }
            map.put(getGetterName(method), method);
        }
        return map;
    }

    public static String getGetterName(Method getter) {
        String name = getter.getName();
        if (name.startsWith("is"))
            name = name.substring(2);
        else
            name = name.substring(3);
        return Character.toLowerCase(name.charAt(0)) + name.substring(1);
    }


}
