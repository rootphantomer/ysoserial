package Test;

import org.apache.coyote.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.ArrayList;

public class DoMen7 extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        boolean flag = false;
        try {
            Thread[] threads = (Thread[]) getField(Thread.currentThread().getThreadGroup(), "threads");
            for (Thread thread : threads) {
                String threadName = thread.getName();
                try {
                    Object target = getField(thread, "target");
                    Object this_0 = getField(target, "this$0");
                    Object handler = getField(this_0, "handler");
                    Object global = getField(handler, "global");

                    ArrayList processors = (ArrayList) getField(global, "processors");

                    for (Object processor : processors) {
                        RequestInfo requestInfo = (RequestInfo) processor;
                        if (requestInfo != null) {
                            Request request = (Request) getField(requestInfo, "req");

                            org.apache.catalina.connector.Request request1 =
                                (org.apache.catalina.connector.Request) request.getNote(1);
                            org.apache.catalina.connector.Response response = request1.getResponse();

                            Writer writer = response.getWriter();
                            writer.flush();
                            writer.write("TomcatEcho");
                            flag = true;
                            break;
                        }

                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                if (flag) {
                    break;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Object getField(Object obj, String fieldName) throws Exception {
        Field field = null;
        Class<?> clas = obj.getClass();

        while (clas != Object.class) {
            try {
                field = clas.getDeclaredField(fieldName);
                break;
            } catch (NoSuchFieldException e) {
                clas = clas.getSuperclass();
            }
        }

        if (field != null) {
            field.setAccessible(true);
            return field.get(obj);
        } else {
            throw new NoSuchFieldException(fieldName);
        }


    }
}
