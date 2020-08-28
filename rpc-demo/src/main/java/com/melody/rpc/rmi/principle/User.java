package com.melody.rpc.rmi.principle;

import java.io.IOException;

/**
 * @author zqhuangc
 */
public class User {

    private int age;

    public int getAge() throws IOException {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
