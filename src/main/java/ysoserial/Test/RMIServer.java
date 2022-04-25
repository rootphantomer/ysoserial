package ysoserial.Test;

import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class RMIServer {

    public static void main(String[] args) {
        try {
            HelloImp helloImp = new HelloImp();
            LocateRegistry.createRegistry(1099);
            Naming.bind("rmi://0.0.0.0:1099/hello",helloImp);
            System.out.println("Success Rmi");
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (AlreadyBoundException e) {
            throw new RuntimeException(e);
        }
    }
}
