package com.melody.rpc.rmi.principle;

/**
 * @author zqhuangc
 */
public class UserServer extends User{

    public static void main(String[] args) {
        UserServer userServer =new UserServer();
        userServer.setAge(18);

        User_Skeleton skeleton = new User_Skeleton(userServer);

        skeleton.start();
    }

}
