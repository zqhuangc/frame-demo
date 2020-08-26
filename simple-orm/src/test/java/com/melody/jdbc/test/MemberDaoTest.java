package com.melody.jdbc.test;

import com.melody.orm.OrmConfig;
import com.melody.orm.demo.dao.MemberDao;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ImportResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import com.melody.orm.demo.entity.Member;
import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.List;



@ImportResource(locations = {"classpath*:application-context.xml"})
@ContextConfiguration(classes = OrmConfig.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class MemberDaoTest {

	@Autowired
	MemberDao memberDao;
	
	
	@Test
	//@Ignore
	public void testSelect(){
		List<Member> list = memberDao.selectByName("zhangsan");
		list.forEach(member -> System.out.println(member.toString()));
		System.out.println(JSON.toJSON(list));
	}


	
	
	@Test
	@Ignore
	public void testInsert() throws Exception{
		Member entity = new Member();
		entity.setMname("zhangsan");
		memberDao.insert(entity);
	}
	
	@Test
	@Ignore
	public void testInsertAll() throws Exception{
		Member m1 = new Member();
		m1.setMname("m1");
		
		Member m2 = new Member();
		m2.setMname("m2");
		
		Member m3 = new Member();
		m3.setMname("m3");
		
		List<Member> entityList = new ArrayList<Member>();
		entityList.add(m1);
		entityList.add(m2);
		entityList.add(m3);

        System.out.println(memberDao.insertAll(entityList));
	}

	@Test
    //@Ignore
	public void testInsertAndReturnId()throws Exception{
        Member member = new Member();
        member.setMname("lisi");
        System.out.println(memberDao.insertAndReturnId(member));
    }
	
}
