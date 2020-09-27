package com.melody.rpc.rmi.api;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * @author zqhuangc
 */
public interface IEcho extends Remote{

    String echo(String message) throws RemoteException;
}
