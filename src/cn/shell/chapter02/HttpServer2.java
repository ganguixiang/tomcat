package cn.shell.chapter02;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpServer2 {

    // 关闭命令
    private static final String SHUTDOWN_COMMAND = "/SHUTDOWN";

    // 是否接收到关闭命令
    private boolean shutdown = false;

    public static void main(String[] args) {
        HttpServer2 server = new HttpServer2();
        // 等待客户端连接
        server.await();
    }

    public void await() {
        ServerSocket serverSocket = null;
        int port = 8080;
        try {
            // 建立服务端
            serverSocket = new ServerSocket(port, 1, InetAddress.getByName("127.0.0.1"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 循环等待客户端连接
        while (!shutdown) {
            try {
                // 接受客户端连接
                Socket socket = serverSocket.accept();
                // 获取输入流
                InputStream inputStream = socket.getInputStream();
                // 获取输出流
                OutputStream outputStream = socket.getOutputStream();

                // 新建request对象
                Request request = new Request(inputStream);
                // 解析request对象
                request.parse();

                // 新建response对象
                Response response = new Response(outputStream);
                // 将request对象放入response中
                response.setRequest(request);

                // 判断是请求servlet还是静态资源
                if (request.getUri().startsWith("/servlet/")) {
                    // 请求servlet使用ServletProcessor2来处理请求
                    ServletProcessor2 processor = new ServletProcessor2();
                    processor.process(request, response);
                } else {
                    // 请求静态资源使用StaticResourceProcessor来处理请求
                    StaticResourceProcessor processor = new StaticResourceProcessor();
                    processor.process(request, response);
                }

                // 返回数据
                response.sendStaticResource();

                // 关闭与客户端的连接
                socket.close();

                // 判断是否需要关闭服务
                shutdown = SHUTDOWN_COMMAND.equalsIgnoreCase(request.getUri());
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }

        }

    }

}
