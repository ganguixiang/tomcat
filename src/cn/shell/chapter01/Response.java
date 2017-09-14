package cn.shell.chapter01;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

public class Response {

    private static final int BUFFER_SIZE = 1024;

    Request request;

    OutputStream outputStream;

    public Response(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    /**
     * 返回静态数据
     * @throws IOException
     */
    public void sendStaticResource() throws IOException {
        byte[] bytes = new byte[BUFFER_SIZE];
        FileInputStream fis = null;
        try {
            // 获取文件对象
            File file = new File(HttpServer.WEB_ROOT, request.getUri());
            // 判断文件是否存在，存在则写入输出流中
            if (file.exists()) {
                fis = new FileInputStream(file);
                int ch = fis.read(bytes, 0, BUFFER_SIZE);
                while (-1 != ch) {
                    outputStream.write(bytes, 0, ch);
                    ch = fis.read(bytes, 0, BUFFER_SIZE);
                }
            } else { // 不存在则返回找不到文件提示
                // 找不到文件
                StringBuffer errroMessage = new StringBuffer();
                errroMessage.append("HTTP/1.1 404 File Not Found\r\n");
                errroMessage.append("Content-Type: text/html\r\n");
                errroMessage.append("Contetn-Length: 23\r\n");
                errroMessage.append("\r\n");
                errroMessage.append("<h1>File Not Found</h1>");
                outputStream.write(errroMessage.toString().getBytes());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != fis) {
                fis.close();
            }
        }
    }

}
