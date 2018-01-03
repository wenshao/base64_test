package com.alibaba;

import org.openjdk.jmh.annotations.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
        text = java.util.Base64.getEncoder().encode(bytes);
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
    public void perf_jdk_encode() throws Exception {
        java.util.Base64.getEncoder().encode(bytes);
    }

    @Benchmark
    public void perf_jdk_decode() throws Exception {
        java.util.Base64.getDecoder().decode(text);
    }

    private static ThreadLocal<byte[]> bufCache = new ThreadLocal<byte[]>();
    private static byte[] allocate(int len) {
        byte[] dst = bufCache.get();
        if (dst == null) {
            dst = new byte[len];
        } else if (dst.length < len) {
            dst = new byte[len];
        } else {
            bufCache.set(null);
        }
        return dst;
    }

    private static void release(byte[] buf) {
        bufCache.set(buf);
    }

    @Benchmark
    public void perf_ali_encode() throws Exception {
        com.alibaba.alib.Base64.Encoder encoder = com.alibaba.alib.Base64.getEncoder();

        int len = encoder.outLength(bytes.length);          // dst array size

        byte[] dst = allocate(len);
        int ret = encoder.encode0(bytes, 0, bytes.length, dst);
        release(dst);
    }



    @Benchmark
    public void perf_ali_decode() throws Exception {
        com.alibaba.alib.Base64.Decoder decoder = com.alibaba.alib.Base64.getDecoder();
        int len = decoder.outLength(text, 0, text.length);
        byte[] dst = allocate(len);
        int ret = decoder.decode0(text, 0, text.length, dst);
        release(dst);
    }

//    public static void main(String[] args) throws Exception {
//        Base64_0 tc = new Base64_0();
//        tc.setUp();
//        tc.perf_ali_decode();
//        tc.perf_ali_decode();
//    }
}
