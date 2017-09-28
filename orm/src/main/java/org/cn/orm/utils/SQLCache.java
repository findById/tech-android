package org.cn.orm.utils;

import java.util.HashMap;

public class SQLCache {
    private static HashMap<String, String> map = new HashMap<>();

    public static void put(String key, String value) {
        map.put(key, value);
    }

    public static String get(String key) {
        return map.get(key);
    }
}
