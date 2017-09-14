package cn.shell.chapter01;

import java.io.IOException;
import java.io.InputStream;

public class Request {

    private InputStream inputStream;

    private String uri;

    public Request(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    /**
     * 请求的inputStream转为String对象
     */
    public void parse() {
        StringBuffer request = new StringBuffer(2048);
        int i;
        byte[] buffer = new byte[2048];
        // 读取输入流中的数据并放入request中
        try {
            i = inputStream.read(buffer);
        } catch (IOException e) {
            e.printStackTrace();
            i = -1;
        }
        for (int j = 0; j < i; j++) {
            request.append((char) buffer[j]);
        }
        System.out.println(request.toString());
        // 解析请求的uri
        this.uri = parseUri(request.toString());
    }

    /**
     * 解析请求的uri
     *
     * @param requestString
     * @return
     */
    private String parseUri(String requestString) {
        // 截取第一个空格到第二个空格
        int i1, i2;
        i1 = requestString.indexOf(" ");
        if (-1 != i1) {
            i2 = requestString.indexOf(" ", i1 + 1);
            if (i2 > i1) {
                return requestString.substring(i1 + 1, i2);
            }
        }
        return null;
    }

    public String getUri() {
        return this.uri;
    }

}
