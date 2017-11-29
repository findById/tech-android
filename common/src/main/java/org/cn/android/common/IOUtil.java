package org.cn.android.common;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by chenning on 17-9-25.
 */

public class IOUtil {

    public static String asString(InputStream is, String charset) throws IOException {
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            copyStream(is, baos, new byte[8196]);
            return baos.toString(charset);
        } finally {
            closeQuietly(baos);
        }
    }

    public static long copyStream(InputStream is, OutputStream os, byte[] buffer) throws IOException {
        try {
            long total = 0;
            int len = 0;
            while ((len = is.read(buffer)) >= 0) {
                os.write(buffer, 0, len);
                total += len;
            }
            is.close();
            is = null;
            return total;
        } finally {
            closeQuietly(is, os);
        }
    }

    public static long copyStream(InputStream is, OutputStream os) throws IOException {
        return copyStream(is, os, new byte[8196]);
    }

    public synchronized static void closeQuietly(Closeable... closeables) {
        if (closeables != null && closeables.length > 0) {
            for (Closeable ac : closeables) {
                if (ac != null) {
                    try {
                        ac.close();
                        ac = null;
                    } catch (Exception ignored) {
                    }
                }
            }
        }
    }
}
