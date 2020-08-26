package javax.core.common.jdbc.datasource;


import org.aspectj.lang.JoinPoint;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 动态切换数据源
 * @author zqhuangc
 */
@Component
public class DynamicDataSourceHolder {
	
	// 默认数据源  
    public final static String DEFAULT_SOURCE = null;  
  
    private final static ThreadLocal<String> current = ThreadLocal.withInitial(DataSourceKey.READ::name);

    private static List<Object> dataSourceKeys = new ArrayList<>();
  
    /** 
     * 还原指定切面的数据源
     * @param joinPoint 
     */
    public static void restore(JoinPoint joinPoint) {
        current.set(DEFAULT_SOURCE);
    }
    
    /**
     * 还原当前切面的数据源
     */
    public static void restore() {
        current.set(DEFAULT_SOURCE);
    }  
  
    /**
     * 设置已知名字的数据源
     *
     * @param dataSourceKey
     */
    public static void set(String dataSourceKey) {
        current.set(dataSourceKey);
    }

    /**
     * 根据年份动态设置数据源
     * @param year
     */
	public static void set(int year) {
		current.set("DB_" + year);
	}

    public static void setDataSourceKey(DataSourceKey key) {
        current.set(key.name());
    }

    /**
     * 获取当前正在使用的数据源名字
     * @return String
     */
    public static String getDataSourceKey() {
        return current.get();
    }

    /**
     * 清空数据源
     */
    public static void clearDataSourceKey() {
        current.remove();
    }

    public static List<Object> getDataSourceKeys() {
        return dataSourceKeys;
    }


}
