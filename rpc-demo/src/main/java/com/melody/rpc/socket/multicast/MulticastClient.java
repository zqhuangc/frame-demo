package com.melody.rpc.socket.multicast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author zqhuangc
 */
public class MulticastClient {

    private static AtomicInteger count = new AtomicInteger(0);

    public static void main(String[] args) throws UnknownHostException {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        for (int i = 0; i < 2; i++) {
            executorService.execute(()-> {
                try {
                    new MulticastClient().initClient();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
            });
        }
    }


    public void initClient() throws UnknownHostException {
        InetAddress group = InetAddress.getByName("224.5.6.7");

        try {
            MulticastSocket socket = new MulticastSocket(8888);

            socket.joinGroup(group);  //加到指定的组里面

            byte[] buf=new byte[256];
            int id = count.getAndIncrement();
            System.out.println("client" + id +"启动");
            while(true){
                DatagramPacket msgPacket=new DatagramPacket(buf,buf.length);
                socket.receive(msgPacket);

                String msg=new String(msgPacket.getData());
                System.out.println("client" + id +"接收到的数据："+msg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
