package com.melody.orm.core;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @zqhuangc
 */
public class SqlBuilder {

    /**
     * 构建单条数据插入SQL
     * @param tableName
     * @param params
     * @return
     */
    public String buildForInsert(String tableName, final Map<String, Object> params){
        if(null == tableName || tableName.trim().length() == 0 || params == null || params.isEmpty()){
            return "";
        }

        StringBuffer sb = new StringBuffer();
        sb.append("insert into ").append(tableName);

        final StringBuffer sbKey = new StringBuffer();
        final StringBuffer sbValue = new StringBuffer();

        sbKey.append("(");
        sbValue.append("(");

        // 添加参数
        Set<String> keys = params.keySet();
        int index = 0;

        for (String key:keys) {
            sbKey.append(key);
            sbValue.append(" :").append(key);
            if(index != keys.size() -1) {
                sbKey.append(",");
                sbValue.append(",");
            }
            index++;
        }

        sbKey.append(")");
        sbValue.append(")");

        sb.append(sbKey).append(" VALUES").append(sbValue);

        return sb.toString();


    }

    /**
     * 构建批量插入SQL
     * @param tableName
     * @param fm
     * @param params
     * @param values
     * @return
     */
    public String buildForBatchInsert(String tableName, Map<String, FieldMapping> fm, List<?> params, Object [] values)throws Exception{
        if(null == tableName || tableName.trim().length() == 0 || params == null || params.isEmpty()){
            return "";
        }

        ArrayList<String> allColumn = new ArrayList<String>();
        StringBuffer sb = new StringBuffer();

        sb.append("insert into ").append(tableName);

        StringBuffer valStr = new StringBuffer();

        for (int j = 0; j < params.size(); j ++) {
            if(j > 0 && j < params.size()){
                valStr.append(",");
            }
            valStr.append("(");
            int k = 0;
            for (FieldMapping p : fm.values()) {
                if(!allColumn.contains(p.columnName)){
                    allColumn.add(p.columnName);
                }
                values[(j * fm.size()) + k] = p.getter.invoke(params.get(j));
                if(k > 0 && k < fm.size()){
                    valStr.append(",");
                }
                valStr.append("?");
                k ++;
            }
            valStr.append(")");
        }
        sb.append("(" + allColumn.toString().replaceAll("\\[|\\]", "") + ") values ").append(valStr.toString());
        return sb.toString();
    }

    /**
     * 构建数据变更 SQL
     * @param tableName
     * @param pkName
     * @param params
     * @return
     */
    public String buildForUpdate(String tableName, String pkName, Map<String,Object> params){
        if(null == tableName || tableName.trim().length() == 0 || params == null || params.isEmpty()){
            return "";
        }

        StringBuffer sb = new StringBuffer();
        sb.append("update ").append(tableName).append(" set ");

        //添加参数
        Set<String> keys = params.keySet();
        int index = 0;

        for (String key : keys) {
            sb.append(key).append(" = :").append(key);
            if(index != keys.size() - 1){
                sb.append(",");
            }
            index++;
        }

        sb.append(" where ").append(pkName.toUpperCase()).append(" = :").append(pkName.toUpperCase());
        return sb.toString();
    }
}
