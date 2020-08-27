package com.melody.orm.domain.entity;


public class Test {
    private Integer id;


    private String name;

    public Test(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Test() {

    }

    @Override
    public String toString() {
        return "Test{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }
}