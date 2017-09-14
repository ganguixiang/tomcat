package cn.shell.chapter02;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandler;

/**
 * servlet请求处理器
 * 
 * processor1的处理方式不安全，request和response直接向上强转为HttpServletRequest和HttpServletResponse，
 * 如果我直接使用HttpServletRequest或者HttpServletResponse向下强转为request和response，那么我就可以直接调用
 * request的parse()和getUri()方法还有response的sendStaticResource()方法，这样是很不安全的。所以在这里引入了
 * 装饰器模式，使用requestFacade和responseFacade来保证request的parse()和getUri()方法和response的sendStaticResource()
 * 方法的安全
 */
public class ServletProcessor2 {

    /**
     * 通过servlet的名称反射得到对应的实例，然后调用service()方法
     * @param request
     * @param response
     */
    public void process(Request request, Response response) {
        // 获取请求的uri
        String uri = request.getUri();
        // 从uri从截取请求的servlet名称
        String servletName = uri.substring(uri.lastIndexOf("/"));
        URLClassLoader loader = null;

        try {
            URL[] urls = new URL[1];
            URLStreamHandler streamHandler = null;
            File classPath = new File(Constants.WEB_ROOT);
            String repository = (new URL("file", null, classPath.getCanonicalPath() + File.separator)).toString();
            urls[0] = new URL(null, repository, streamHandler);
            loader = new URLClassLoader(urls);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Class myClass = null;
        try {
            // 根据servlet的名称加载对应的class
            myClass = loader.loadClass(servletName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        Servlet servlet = null;
        // 使用装饰器，保证request中的parse()和getUri()方法的安全性，保证了response中sendStaticResource()的安全性
        RequestFacade requestFacade = new RequestFacade(request);
        ResponseFacade responseFacade = new ResponseFacade(response);

        try {
            // 反射得到servlet实例
            servlet = (Servlet) myClass.newInstance();
            // 调用service()方法
            servlet.service(requestFacade, responseFacade);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
