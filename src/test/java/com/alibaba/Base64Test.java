package com.alibaba;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

public class Base64Test {
    public static void main(String[] args) throws Exception {
        byte[] bytes = loadBytes();
        Base64.getEncoder().encodeToString(bytes);
    }

    public static byte[] loadBytes() throws IOException {
        InputStream is = Thread
                .currentThread()
                .getContextClassLoader()
                .getResource("dingtalk.png")
                .openStream();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buf = new byte[8192];
        for (;;) {
            int len = is.read(buf);
            if (len == -1) {
                break;
            }
            if (len > 0) {
                out.write(buf, 0, len);
            }
        }

        is.close();

        return out.toByteArray();
    }
}
