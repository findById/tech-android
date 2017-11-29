package org.cn.android.common;

/**
 * Created by chenning on 17-11-29.
 */

public class JSONUtil {

    public static String format(String str) {
        String LINE_SEPARATOR = "\n";
        int level = 0;
        char current = '\0';
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            current = str.charAt(i);
            switch (current) {
                case '{':
                case '[':
                    buffer.append(current).append(LINE_SEPARATOR);
                    level++;
                    buffer.append(getLevel(level));
                    break;
                case '}':
                case ']':
                    buffer.append(LINE_SEPARATOR);
                    level--;
                    buffer.append(getLevel(level));
                    buffer.append(current);
                    break;
                case ',':
                    buffer.append(current);
                    if (str.charAt(i - 1) == '"' || str.charAt(i + 1) == '"') {
                        buffer.append(LINE_SEPARATOR);
                        buffer.append(getLevel(level));
                    }
                    break;
                case ':':
                    buffer.append(current);
                    if (str.charAt(i + 1) != ' ') {
                        buffer.append(' ');
                    }
                    break;
                default:
                    buffer.append(current);
                    break;
            }
        }
        return buffer.toString();
    }

    private static String getLevel(int level) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < level; i++) {
            sb.append("    ");
        }
        return sb.toString();
    }
}
