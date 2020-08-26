package javax.core.common.jdbc.datasource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * 动态数据源
 * @author zqhuangc
 */
public class DynamicDataSource extends AbstractRoutingDataSource {

    Logger LOG = LoggerFactory.getLogger(DynamicDataSource.class);

    @Override  
    protected Object determineCurrentLookupKey() {
        LOG.info("当前数据源 [{}]", DynamicDataSourceHolder.getDataSourceKey());
        return DynamicDataSourceHolder.getDataSourceKey();
    }
    
}
