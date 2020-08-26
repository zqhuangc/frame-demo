package com.melody.orm.demo.entity;


import java.io.Serializable;
import java.math.BigInteger;
import javax.persistence.*;

@Entity
@Table(name="t_member")
public class Member implements Serializable{
	@Id
	//@Transient
	private Long id;

	@Column(name="name")
	private String mname;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMname() {
		return mname;
	}

	public void setMname(String name) {
		this.mname = name;
	}

	@Override
	public String toString() {
		return "Member{" +
				"id=" + id +
				", mname='" + mname + '\'' +
				'}';
	}
}
