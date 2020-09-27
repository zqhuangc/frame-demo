package com.melody.rpc.rmi.principle;

import java.io.IOException;

/**
 * @author zqhuangc
 */
public class UserClient {

    public static void main(String[] args) throws IOException {
        User user = new User_Stub();

        int age=user.getAge();

        System.out.println(age);
    }
}
