package com.melody.orm.demo.dao;


import com.melody.orm.core.BaseDaoSupport;
import com.melody.orm.core.QueryRule;
import com.melody.orm.demo.entity.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.math.BigInteger;
import java.util.List;

@Repository
public class MemberDao extends BaseDaoSupport<Member, Long> {

	@Autowired
	public void setDataSource(@Qualifier("dynamicDataSource")DataSource dataSource) {
		this.setDataSourceRead(dataSource);
		this.setDataSourceWrite(dataSource);
	}
	
	/**
	 * 
	 * @param name
	 * @return
	 */
	public List<Member> selectByName(String name){
		//构建一个QureyRule 查询规则
		QueryRule queryRule = QueryRule.getInstance();
		//查询一个name= 赋值 结果，List
		queryRule.andEqual("name", name);
		//相当于自己再拼SQL语句
		return super.select(queryRule);
	}
	
	/**
	 * 
	 */
	public int insert(Member entity) throws Exception{
		return super.insert(entity);
	}
	
	/**
	 * @throws Exception 
	 * 
	 */
	public int insertAll(List<Member> entityList) throws Exception{
		return super.insertAll(entityList);
	}

	public Long insertAndReturnId(Member entity)throws Exception{
	    return super.insertAndReturnId(entity);
    }
	
}
