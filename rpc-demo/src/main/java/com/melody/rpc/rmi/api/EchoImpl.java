package com.melody.rpc.rmi.api;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * @author zqhuangc
 */
public class EchoImpl extends UnicastRemoteObject implements IEcho {

    public EchoImpl() throws RemoteException {
    }


    @Override
    public String echo(String message) throws RemoteException {
        return "echo message->"+ message;
    }
}
