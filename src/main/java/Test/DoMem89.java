package Test;

import org.apache.catalina.connector.Connector;
import org.apache.catalina.connector.Request;
import org.apache.catalina.core.StandardContext;
import org.apache.coyote.RequestInfo;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.ArrayList;

public class DoMem89 extends HttpServlet {

//    WebappClassLoaderBase —> ApplicationContext(getResources().getContext()) —>
//    StandardService—>Connector—>AbstractProtocol$ConnectoinHandler—>RequestGroupInfo(global)
//    —>RequestInfo——->Request——–>Response

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        boolean flag = false;
        try {
            org.apache.catalina.loader.WebappClassLoaderBase webappClassLoaderBase =
                (org.apache.catalina.loader.WebappClassLoaderBase) Thread.currentThread().getContextClassLoader();
            StandardContext standardContext = (StandardContext) webappClassLoaderBase.getResources().getContext();
            Connector[] connectors = (Connector[]) getField(getField(getField(standardContext, "context"), "service")
                , "connectors");

            for (Connector connector : connectors) {
                Object global = getField(getField(getField(connector, "protocolHandler"), "handler"), "global");
                ArrayList processors = (ArrayList) getField(global, "processors");
                for (Object processor : processors) {
                    RequestInfo requestInfo = (RequestInfo) processor;
                    if (requestInfo != null) {
                        Request request = (Request) getField(requestInfo, "req");

                        org.apache.catalina.connector.Request Myrequest =
                            (org.apache.catalina.connector.Request) request.getNote(String.valueOf(1));
                        org.apache.catalina.connector.Response Myresponse = Myrequest.getResponse();
                        Writer writer = Myresponse.getWriter();
                        writer.flush();
                        writer.write("TomcatEcho");
                        flag = true;
                        break;

                    }
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
