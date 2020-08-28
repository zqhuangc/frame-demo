package com.melody.rpc.rmi.api;

import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

/**
 * @author zqhuangc
 */
public class EchoServer {

    public static void main(String[] args) {
        try {
            IEcho provider = new EchoImpl();

            LocateRegistry.createRegistry(8888);

            Naming.bind("rmi://localhost:8888/echo", provider);

            System.out.println("server start success");
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (AlreadyBoundException e) {
            e.printStackTrace();
        }
    }
}
