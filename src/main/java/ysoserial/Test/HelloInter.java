package ysoserial.Test;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface HelloInter extends Remote {
    String Hello(String age) throws RemoteException;
    void Eeyi(Object obj) throws RemoteException;
}
