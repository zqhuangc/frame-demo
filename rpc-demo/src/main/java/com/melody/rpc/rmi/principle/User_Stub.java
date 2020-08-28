package com.melody.rpc.rmi.principle;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * @author zqhuangc
 */
public class User_Stub extends User{

    private Socket socket;

    public User_Stub() throws IOException {
        socket = new Socket("localhost",8888);
    }

    public int getAge() throws IOException {
        ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());

        // 调用远程方法
        outputStream.writeObject("getAge");
        outputStream.flush();

        // 调用结果
        ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
        int age = objectInputStream.readInt();
        return age;
    }
}
