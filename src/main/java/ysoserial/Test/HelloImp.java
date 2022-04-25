package ysoserial.Test;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class HelloImp extends UnicastRemoteObject implements HelloInter {

    private static final long serialVersionUID = 1L;
    protected HelloImp() throws RemoteException {
        super();
    }

    @Override
    public String Hello(String age) throws RemoteException {

//        try {
//           Runtime.getRuntime().exec("calc");
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
        return "successHello" + age;
    }

    @Override
    public void Eeyi(Object obj) throws RemoteException {
        System.out.println("subcccccEeyi" + obj);
    }
}
