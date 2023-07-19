package Test;


import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Field;

public class DoFilter extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
       try {
           String name = "RoboTerh";
           //从request中获取ServletContext
           ServletContext servletContext = req.getSession().getServletContext();

           //从context中获取ApplicationContext对象
           Field appctx = servletContext.getClass().getDeclaredField("context");
       } catch (NoSuchFieldException e) {
           throw new RuntimeException(e);
       }
    }
}
