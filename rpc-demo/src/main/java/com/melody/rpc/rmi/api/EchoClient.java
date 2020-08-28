package com.melody.rpc.rmi.api;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

/**
 * @author zqhuangc
 */
public class EchoClient {

    public static void main(String[] args) {
        try {
            IEcho consumer = (IEcho) Naming.lookup("rmi://localhost:8888/echo");
            System.out.println(consumer.toString());
            System.out.println(consumer.echo("this is a test"));
        } catch (NotBoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
