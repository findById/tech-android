package org.cn.android.common;

import java.security.MessageDigest;

/**
 * Created by chenning on 17-11-29.
 */

public class DigestUtil {

    public static String md5(String text) {
        return encode(text, "MD5", "UTF-8");
    }

    public static String sha1(String text) {
        return encode(text, "SHA-1", "UTF-8");
    }

    public static String sha256(String text) {
        return encode(text, "SHA-256", "UTF-8");
    }

    public static String sha512(String text) {
        return encode(text, "SHA-512", "UTF-8");
    }

    public static String encode(String text, String algorithm, String charset) {
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            md.update(text.getBytes(charset));
            byte[] buf = md.digest();
            StringBuffer sb = new StringBuffer();
            for (byte b : buf) {
                if (Integer.toHexString(0xFF & b).length() == 1) {
                    sb.append("0").append(Integer.toHexString(0xFF & b));
                } else {
                    sb.append(Integer.toHexString(0xFF & b));
                }
            }
            return sb.toString();
        } catch (Throwable e) {
        }
        return "";
    }
}
