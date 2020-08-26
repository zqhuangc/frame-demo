package com.melody.orm.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.core.common.Page;
import javax.core.common.jdbc.datasource.DataSourceKey;
import javax.core.common.jdbc.datasource.DynamicDataSourceHolder;
import javax.core.common.utils.GenericsUtils;
import javax.core.common.utils.StringUtils;
import javax.sql.DataSource;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * @author zqhuangc
 */
public abstract class BaseDaoSupport<T extends Serializable, PK extends Serializable> {

    Logger LOG = LoggerFactory.getLogger(this.getClass());

    private DataSource dataSourceWrite;
    private DataSource dataSourceRead;

    private JdbcTemplate jdbcTemplateWrite;
    private JdbcTemplate jdbcTemplateRead;
    

    private String tableName;
    private EntityOperation<T> eo;

    public BaseDaoSupport() {
        Class entityClass = GenericsUtils.getSuperClassGenricType(getClass(), 0);
        eo = new EntityOperation<T>(entityClass);
        this.tableName = eo.tableName;
    }

    protected void setDataSourceWrite(DataSource dataSourceWrite){
        this.dataSourceWrite = dataSourceWrite;
        jdbcTemplateWrite = new JdbcTemplate(dataSourceWrite);
    }

    protected void setDataSourceRead(DataSource dataSourceRead){
        this.dataSourceRead = dataSourceRead;
        jdbcTemplateRead = new JdbcTemplate(dataSourceRead);
    }

    public JdbcTemplate getJdbcTemplateWrite() {
        return jdbcTemplateWrite;
    }

    public JdbcTemplate getJdbcTemplateRead() {
        return jdbcTemplateRead;
    }

    /**
     * 根据主键值，获取一个对象
     * @param pk
     * @return
     * @throws Exception
     */
    protected T get(PK pk)throws Exception{
        QueryRule queryRule = QueryRule.getInstance();
        queryRule.andEqual(this.eo.pkColumn, pk);
        return selectUnique(queryRule);
    }

    protected long getCount(QueryRule queryRule) throws Exception{
        QueryRuleBuilder builder = new QueryRuleBuilder(queryRule);
        String whereSql = builder.getWhereSql();
        StringBuffer sql = new StringBuffer("select count(1) from " + this.getTableName());
        if(!(whereSql == null || whereSql.trim().length() == 0)){
            sql.append(" where " + whereSql);
        }
        return this.jdbcTemplateRead.query(sql.toString(),builder.getValueArr(), this.eo.rowMapper).size();
    }

    /**
     * 根据查询条件获取唯一的记录
     * @param queryRule
     * @return
     */
    protected T selectUnique(QueryRule queryRule){
        List<T> r = this.select(queryRule);
        return r == null? null : r.get(0);
    }

    /**
     * 根据查询条件获得一个对象
     * @param queryRule
     * @return
     */
    protected List<T> select(QueryRule queryRule){
        QueryRuleBuilder builder = new QueryRuleBuilder(queryRule);
        StringBuilder sqlStr = buildSelect(builder);
        return this.jdbcTemplateRead.query(sqlStr.toString(), builder.getValueArr(), this.eo.rowMapper);

    }

    protected StringBuilder buildSelect(QueryRuleBuilder builder){
        StringBuilder sqlStr = new StringBuilder();
        sqlStr.append("select " + this.eo.allColumn + " from " + this.getTableName());
        String whereSql = builder.getWhereSql();
        String orderSql = builder.getOrderSql();
        if(!(whereSql == null || whereSql.trim().length() == 0)){
            sqlStr.append(" where " + whereSql);
        }
        if(!(orderSql == null || orderSql.trim().length() == 0)){
            sqlStr.append(" order by " + orderSql);
        }
        return sqlStr;
    }

    protected Page<T> select(QueryRule queryRule, int pageNo, int pageSize)throws Exception{
        long count = this.getCount(queryRule);
        if(count == 0){
            return new Page<T>();
        }
        long start = (pageNo - 1) * pageSize;

        // 有数据，则继续查询
        QueryRuleBuilder builder = new QueryRuleBuilder(queryRule);
        StringBuilder sql = buildSelect(builder);

        sql.append(" limit " + start + "," +pageSize);

        LOG.debug(sql.toString());

        List<T> list = this.jdbcTemplateRead.query(sql.toString(), builder.getValueArr(), this.eo.rowMapper);
        return new Page<T>(start, count, pageSize, list);
    }

    /**
     * 根据sql语句查询
     * @param sql
     * @param params
     * @return
     * @throws Exception
     */
    protected List<Map<String, Object>> selectBySql(String sql, List<Object> params)throws  Exception{
        return this.jdbcTemplateRead.queryForList(sql, params.toArray());
    }

    /**
     * 插入一条新记录
     * @param entity
     * @return
     * @throws Exception
     */
    protected int insert(T entity)throws Exception {
        SqlBuilder builder = new SqlBuilder();
        NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(this.dataSourceWrite);
        Map<String, Object> params = this.eo.parse(entity);
        String sql = builder.buildForInsert(this.getTableName(), params);
        //return this.jdbcTemplateWrite.update(sql, params);
        return jdbcTemplate.update(sql, params);
    }

    /**
     * 批量插入
     * @param entityList
     * @return
     * @throws Exception
     */
    protected int insertAll(List<T> entityList)throws  Exception{
        if(null == entityList || entityList.size() == 0){
            return 0;
        }
        int count = 0, len = entityList.size(), step = 50000;
        int maxPage = (len % step == 0) ? (len / step) : (len / step + 1);
        SqlBuilder builder = new SqlBuilder();
        for (int i = 1; i <= maxPage; i++){
            Page<T> page = pagination(entityList, i, step);
            Object[] values = new Object[this.eo.mappings.size() * page.getRows().size()];
            String sql = builder.buildForBatchInsert(this.getTableName(), this.eo.mappings, page.getRows(), values);
            int result = this.jdbcTemplateWrite.update(sql, values);
            count += result;
        }
        return count;
    }

    /**
     * 插入一条新的记录，并返回新插入记录的id
     * @param entity
     * @return
     * @throws Exception
     */
    protected PK insertAndReturnId(T entity)throws Exception{
        SqlBuilder builder = new SqlBuilder();
        Map<String, Object> params = this.eo.parse(entity);

        final String sql = builder.buildForInsert(this.getTableName(), params);

        final List<Object> values = new ArrayList<>(params.size());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(this.dataSourceWrite);
            jdbcTemplate.update(sql, new MapSqlParameterSource(params), keyHolder);
        } catch (DataAccessException e) {
            e.printStackTrace();
        }

        if(null == keyHolder){
            return null;
        }

        Map<String, Object> keys = keyHolder.getKeys();
        if(keys == null || keys.size() == 0 || keys.values().size() == 0){
            return null;
        }

        BigInteger key = (BigInteger)keys.values().toArray()[0];
        if(key == null || !(key instanceof Serializable)){
            return null;
        }

        if(key != null){
            // keyHolder.getKey().longValue();
            return (PK) this.eo.pkField.getType().cast(key.longValue());
        }else{
            return null;
        }
    }

    /**
     * 更新一条记录
     * @param entity
     * @return
     * @throws Exception
     */
    protected int update(T entity)throws Exception{
        SqlBuilder builder = new SqlBuilder();
        Map<String, Object> params = this.eo.parse(entity);
        String sql = builder.buildForUpdate(this.getTableName(), this.eo.pkColumn, params);
        NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(this.dataSourceWrite);
        //return this.jdbcTemplateWrite.update(sql, params);
        return jdbcTemplate.update(sql, params);
    }

    /**
     * 根据主键更新一条记录，如果记录存在则覆盖，如果记录不存在则插入
     * @param entity
     * @return
     * @throws Exception
     */
    protected int replace(T entity)throws Exception{
        return 0;
    }

    /**
     * 批量插入
     * @param entityList
     * @return
     * @throws Exception
     */
    protected int replaceAll(List<T> entityList)throws Exception{
        return 0;
    }

    /**
     * 根据当前list进行相应的分页返回
     * @param objList
     * @param pageNo
     * @param pageSize
     * @return
     * @throws Exception
     */
    protected Page<T> pagination(List<T> objList, int pageNo, int pageSize) throws Exception{
        List<T> objects = new ArrayList<T>(0);
        int startIndex = (pageNo - 1) * pageSize;
        int endIndex = pageNo * pageSize;
        if(endIndex >= objList.size()){
            endIndex = objList.size();
        }
        for (int i = startIndex; i < endIndex; i++){
            objects.add(objList.get(i));
        }

        return new Page<T>(startIndex, objList.size(), pageSize, objects);

    }



    /**
     * 动态切换表名
     * @param tableName
     */
    protected void setTableName(String tableName) {
        if(StringUtils.isEmpty(tableName)){
            this.tableName = this.eo.tableName;
        }else{
            this.tableName = tableName;
        }
    }

    /**
     * 获取tableName
     * @return
     */
    protected String getTableName(){
        return null == this.tableName ? this.eo.tableName : this.tableName;
    }

    /**
     * 重置实体类操作对应的表名
     */
    protected void restoreTableName(){
        this.tableName = this.eo.tableName;
    }

}
