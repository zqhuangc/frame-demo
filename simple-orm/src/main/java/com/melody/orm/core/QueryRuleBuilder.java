package com.melody.orm.core;

import cn.hutool.core.util.ArrayUtil;
import org.springframework.util.Assert;

import javax.core.common.utils.StringUtils;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 根据 QueryRule 自动构建 SQL
 * @author zqhuangc
 */
public class QueryRuleBuilder {

    private int CURR_INDEX = 0;//记录参数索引位置
    private List<String> properties;//保存列名列表
    private List<Object> values;//保存参数列表
    private List<Order> orders;//保存排序规则列表

    private String whereSql = "";
    private String orderSql = "";
    private Object[] valueArr = new Object[]{};
    private Map<Object,Object> valueMap = new HashMap<>();


    /**
     * 获取查询条件
     * @return
     */
    public String getWhereSql() {
        return removeFirstAndForWhere(this.whereSql);
    }

    /**
     * 获取排序条件
     * @return
     */
    public String getOrderSql() {
        return this.orderSql;
    }

    /**
     * 获得参数值列表
     * @return
     */
    public Object[] getValueArr() {
        return valueArr;
    }

    /**
     * 获得参数值列表
     * @return
     */
    public Map<Object, Object> getValueMap() {
        return valueMap;
    }

    /**
     * 创建SQL构造器
     * @param queryRule
     */
    public QueryRuleBuilder(QueryRule queryRule){
        CURR_INDEX = 0;
        properties = new ArrayList<>();
        values = new ArrayList<>();
        orders = new ArrayList<>();

        for (QueryRule.Rule rule : queryRule.getRuleList()) {
            switch (rule.getType()){
                case QueryRule.LIKE:
                    processLike(rule);
                    break;
                case QueryRule.BETWEEN:
                    processBetween(rule);
                    break;
                case QueryRule.IN:
                    processIN(rule);
                    break;
                case QueryRule.NOTIN:
                    processNotIN(rule);
                    break;
                case QueryRule.EQ:
                    processEqual(rule);
                    break;
                case QueryRule.NOTEQ:
                    processNotEqual(rule);
                    break;
                case QueryRule.GT:
                    processGreaterThen(rule);
                    break;
                case QueryRule.GE:
                    processGreaterEqual(rule);
                    break;
                case QueryRule.LT:
                    processLessThen(rule);
                    break;
                case QueryRule.LE:
                    processLessEqual(rule);
                    break;

                case QueryRule.ISEMPTY:
                    processIsEmpty(rule);
                    break;
                case QueryRule.ISNOTEMPTY:
                    processIsNotEmpty(rule);
                    break;
                case QueryRule.ISNULL:
                    processIsNull(rule);
                    break;
                case QueryRule.ISNOTNULL:
                    processIsNotNull(rule);
                    break;
                case QueryRule.ASC_ORDER:
                    processOrder(rule,true);
                    break;
                case QueryRule.DESC_ORDER:
                    processOrder(rule, false);
                    break;
                default:
                    throw new IllegalArgumentException("type " + rule.getType() +" not supported.");

            }
        }

        //拼装 where 语句
        appendWhereSql();
        //拼装排序语句
        appendOrderSql();
        //拼装参数值
        appendValues();
    }

    /**
     * 处理 like
     * @param rule
     */
    private void processLike(QueryRule.Rule rule){
        if(ArrayUtil.isEmpty(rule.getValues())){
            return;
        }
        Object obj = rule.getValues()[0];
        if(null != obj){
            String value = obj.toString();
            if(!StringUtils.isEmpty(value)){
                value = value.replace('*','%');
                obj = value;
            }
        }
        add(rule.getAndOr(),rule.getPropertyName(), "like", "%" + rule.getValues()[0] +"%");
    }

    /**
     * 处理 between
     * @param rule
     */
    private void processBetween(QueryRule.Rule rule){
        if(ArrayUtil.isEmpty(rule.getValues()) || rule.getValues().length < 2){
            return;
        }
        add(rule.getAndOr(), rule.getPropertyName(), "","between", rule.getValues()[0],"and");
        add(0, "", "", "",rule.getValues()[1], "");
    }

    /**
     * 处理 =
     * @param rule
     */
    private void processEqual(QueryRule.Rule rule){
        if(ArrayUtil.isEmpty(rule.getValues())){
            return;
        }
        add(rule.getAndOr(), rule.getPropertyName(), "=", rule.getValues()[0]);
    }

    /**
     * 处理 <>
     * @param rule
     */
    private void processNotEqual(QueryRule.Rule rule){
        if(ArrayUtil.isEmpty(rule.getValues())){
            return;
        }
        add(rule.getAndOr(), rule.getPropertyName(), "<>", rule.getValues()[0]);
    }

    /**
     * 处理 >
     * @param rule
     */
    private void processGreaterThen(QueryRule.Rule rule){
        if(ArrayUtil.isEmpty(rule.getValues())){
            return;
        }
        add(rule.getAndOr(), rule.getPropertyName(), ">", rule.getValues()[0]);
    }

    /**
     * 处理 >=
     * @param rule
     */
    private void processGreaterEqual(QueryRule.Rule rule){
        if(ArrayUtil.isEmpty(rule.getValues())){
            return;
        }
        add(rule.getAndOr(), rule.getPropertyName(), ">=", rule.getValues()[0]);
    }

    /**
     * 处理 <
     * @param rule
     */
    private void processLessThen(QueryRule.Rule rule){
        if(ArrayUtil.isEmpty(rule.getValues())){
            return;
        }
        add(rule.getAndOr(), rule.getPropertyName(), ">", rule.getValues()[0]);
    }

    /**
     * 处理 <=
     * @param rule
     */
    private void processLessEqual(QueryRule.Rule rule){
        if(ArrayUtil.isEmpty(rule.getValues())){
            return;
        }
        add(rule.getAndOr(), rule.getPropertyName(), "<=", rule.getValues()[0]);
    }

    /**
     * 处理 =''
     * @param rule
     */
    private void processIsEmpty(QueryRule.Rule rule){
        add(rule.getAndOr(), rule.getPropertyName(), "=","''");
    }

    /**
     * 处理 <>''
     * @param rule
     */
    private void processIsNotEmpty(QueryRule.Rule rule){
        add(rule.getAndOr(), rule.getPropertyName(), "<>","''");
    }

    /**
     * 处理 is null
     * @param rule
     */
    private void processIsNull(QueryRule.Rule rule){
        add(rule.getAndOr(), rule.getPropertyName(), "is null",null);
    }

    /**
     * 处理 is not null
     * @param rule
     */
    private void processIsNotNull(QueryRule.Rule rule){
        add(rule.getAndOr(), rule.getPropertyName(), "is not null",null);
    }

    /**
     * 处理 in
     * @param rule
     */
    private void processIN(QueryRule.Rule rule){
        inAndNotIn(rule, "in");
    }

    /**
     * 处理 not in
     * @param rule
     */
    private void processNotIN(QueryRule.Rule rule){
        inAndNotIn(rule, "not in");
    }

    /**
     * 处理 in 和 not in
     * List or Array
     * @param rule
     * @param inStr
     */
    private void inAndNotIn(QueryRule.Rule rule, String inStr){
        if (ArrayUtil.isEmpty(rule.getValues())) {
            return;
        }
        if((rule.getValues().length == 1) && (rule.getValues()[0] != null)
                && (rule.getValues()[0] instanceof List)){
            List<Object> list = (List) rule.getValues()[0];
            if(list != null && list.size() > 1){
                for(int i = 0, length = list.size(); i < length; i ++){
                    if(i == 0 && i == length - 1){
                        add(rule.getAndOr(), rule.getPropertyName(), "", inStr + " (" ,list.get(i), ")");
                    }else if(i == 0 && i < length - 1){
                        add(rule.getAndOr(), rule.getPropertyName(), "",inStr +" (", list.get(i), "");
                    }

                    if(i > 0 && i < length - 1){
                        add(0,"",",","", list.get(i), "");
                    }

                    if(i == length - 1 && i != 0){
                        add(0,"",",","",list.get(i), ")");
                    }
                }
            }
        }else{
            Object[] list = rule.getValues();
            for(int i = 0, length = list.length; i < length; i ++){
                if(i == 0 && i == length - 1){
                    add(rule.getAndOr(), rule.getPropertyName(), "", inStr + " (" ,list[i], ")");
                }else if(i == 0 && i < length - 1){
                    add(rule.getAndOr(), rule.getPropertyName(), "",inStr +" (", list[i], "");
                }

                if(i > 0 && i < length - 1){
                    add(0,"",",","", list[i], "");
                }

                if(i == length - 1 && i != 0){
                    add(0,"",",","",list[i], ")");
                }
            }
        }
    }

    /**
     * 处理排序
     * @param rule
     */
    private void processOrder(QueryRule.Rule rule, boolean asc){
        if (StringUtils.isEmpty(rule.getPropertyName())){
            return;
        }
        if(asc){
            add(rule.getAndOr(), rule.getPropertyName(), "order by", "", Order.asc(rule.getPropertyName()), "asc");
        }else{
            add(rule.getAndOr(), rule.getPropertyName(), "order by", "", Order.desc(rule.getPropertyName()), "desc");
        }

    }

    /**
     * 加入到 sql 查询规则队列
     * @param andOr and 或者 or
     * @param key 列名
     * @param split 列名与值之间的间隔
     * @param value 值
     */
    private void add(int andOr, String key, String split, Object value){
        add(andOr, key, split, "", value, "");
    }

    /**
     * 加入到 sql 查询规则队列
     * @param andOr and 或者 or
     * @param key 列名
     * @param split 列名与值之间的间隔
     * @param prefix 值前缀
     * @param value 值
     * @param suffix 值后缀
     */
    private void add(int andOr, String key, String split, String prefix, Object value, String suffix){
        String andOrStr = (0 == andOr? "" : (QueryRule.AND == andOr ? "and" : "or"));
        properties.add(CURR_INDEX, andOrStr + key + " " + split + prefix + (null != value? " ? " : " ") + suffix);
        if(null != value){
            values.add(CURR_INDEX, value);
            CURR_INDEX ++;
        }
    }

    /**
     * 去掉 select
     * @param sql
     * @return
     */
    protected String removeSelect(String sql){
        if(sql.toLowerCase().matches("from\\s+")){
            int beginIndex = sql.toLowerCase().indexOf("from");
            return sql.substring(beginIndex);
        }else{
            return sql;
        }
    }

    /**
     * 去掉 order
     * @param sql
     * @return
     */
    protected String removeOrders(String sql){
        Pattern p = Pattern.compile("order\\s*by[\\w|\\W|\\s|\\S]*",Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(sql);
        StringBuffer sb = new StringBuffer();
        while(m.find()){
            m.appendReplacement(sb, "");
        }
        m.appendTail(sb);
        return sb.toString();
    }

    /**
     * 拼装 where 语句
     */
    private void appendWhereSql(){
        StringBuffer whereSql = new StringBuffer();
        for (String p: properties) {
            whereSql.append(p);
        }
        this.whereSql = removeSelect(removeOrders(whereSql.toString()));
    }

    /**
     * 拼装 排序 语句
     */
    private void appendOrderSql(){
        StringBuffer orderSql = new StringBuffer();
        for (int i = 0, length = orders.size(); i < length; i++) {
            if(i > 0 && i < length - 1){
                orderSql.append(",");
            }
            orderSql.append(orders.get(i).toString());
        }
        this.orderSql = removeSelect(removeOrders(orderSql.toString()));
    }

    /**
     * 拼装参数值
     */
    private void appendValues(){
        Object[] val = new Object[values.size()];
        for(int i = 0, length = values.size(); i< length; i++){
            val[i] = values.get(i);
            valueMap.put(i,values.get(i));
        }
        this.valueArr = val;
    }



    /**
     * 处理
     * @param sql
     * @return
     */
    private String removeFirstAndForWhere(String sql){
        if(null == sql){
            return null;
        }
        return sql.trim().toLowerCase().replaceAll("^\\s*and","") + " ";

    }


}
