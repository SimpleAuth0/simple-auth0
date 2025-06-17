package io.github.simpleauth0.core.http;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * @author: ReLive27
 * @date: 2025/6/2 21:02
 */
public class RepeatedlyRequestWrapper extends HttpServletRequestWrapper {
    private final byte[] body;

    public RepeatedlyRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        request.setCharacterEncoding("UTF-8");
        this.body = readBody(request);
    }

    private byte[] readBody(HttpServletRequest request) throws IOException {
        try (InputStream is = request.getInputStream();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = is.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            return baos.toByteArray();
        }
    }

    @Override
    public BufferedReader getReader() {
        return new BufferedReader(new InputStreamReader(getInputStream(), StandardCharsets.UTF_8));
    }

    @Override
    public ServletInputStream getInputStream() {
        final ByteArrayInputStream bais = new ByteArrayInputStream(body);

        return new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return bais.available() == 0;
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
                // 不支持异步读取
                throw new UnsupportedOperationException();
            }

            @Override
            public int read() {
                return bais.read();
            }
        };
    }

    public String getBodyString() {
        return new String(body, StandardCharsets.UTF_8);
    }
}
