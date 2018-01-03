package com.alibaba;

import com.sun.xml.internal.rngom.parse.host.Base;
import org.openjdk.jmh.annotations.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

import static org.openjdk.jmh.annotations.Mode.AverageTime;

@State(value = Scope.Benchmark)
@BenchmarkMode(AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class Base64_0 {
    private byte[] bytes;
    private byte[] text;

    @Setup
    public void setUp() throws Exception {
        bytes = loadBytes();
        text = Base64.getEncoder().encode(bytes);
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

    @Benchmark
    public void perf_encode() throws Exception {
        Base64.getEncoder().encodeToString(bytes);
    }

    @Benchmark
    public void perf_decode() throws Exception {
        Base64.getDecoder().decode(text);
    }
}
