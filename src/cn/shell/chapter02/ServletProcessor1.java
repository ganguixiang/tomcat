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
 */
public class ServletProcessor1 {

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

        try {
            // 反射得到servlet实例
            servlet = (Servlet) myClass.newInstance();
            // 调用service()方法
            servlet.service(request, response);
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
