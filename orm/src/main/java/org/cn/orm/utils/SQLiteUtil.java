package org.cn.orm.utils;

import android.database.Cursor;
import android.text.TextUtils;

import org.cn.orm.annotation.Column;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class SQLiteUtil {

    /**
     * 读取游标中的数据
     */
    public static Object invokeMethod(Class<?> clazz, Cursor cursor) {
        Object object = null;
        try {
            object = clazz.newInstance();
            Method[] methods = clazz.getMethods();
            for (Method method : methods) {
                if (method.getName().startsWith("set")) {
                    method.invoke(object, getValueByMethod(method, cursor));
                }
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } finally {
        }
        return object;
    }

    /**
     * 读取游标中的数据
     */
    public static Object invokeField(Class<?> clazz, Cursor cursor) {
        Object object = null;
        try {
            object = clazz.newInstance();
            Field[] fields = clazz.getFields();
            for (Field field : fields) {
                Column column = field.getAnnotation(Column.class);
                if (column != null) {
                    Object obj = getValueByField(column, field, cursor);
                    // Log.d("ORM", field.getName() + "   " + obj);
                    if (obj != null) {
                        field.set(object, obj);
                    }
                }
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } finally {
        }
        return object;
    }

    /**
     * 通过method (setter) 获取游标中的数据
     */
    private static Object getValueByMethod(Method method, Cursor cursor) {
        Class<?> params = method.getParameterTypes()[0];
        String column = method.getName().substring(3, method.getName().length());
        column = new StringBuffer(column).replace(0, 1, column.toLowerCase().substring(0, 1)).toString();
        if (String.class.isAssignableFrom(params)) {
            return cursor.getString(cursor.getColumnIndex(column));
        } else if (int.class.isAssignableFrom(params)) {
            return cursor.getInt(cursor.getColumnIndex(column));
        } else if (long.class.isAssignableFrom(params)) {
            return cursor.getLong(cursor.getColumnIndex(column));
        } else if (double.class.isAssignableFrom(params)) {
            return cursor.getDouble(cursor.getColumnIndex(column));
        } else if (float.class.isAssignableFrom(params)) {
            return cursor.getFloat(cursor.getColumnIndex(column));
        } else if (boolean.class.isAssignableFrom(params)) {
            return (cursor.getInt(cursor.getColumnIndex(column)) == 1);
        } else if (byte[].class.isAssignableFrom(params)) {
            return cursor.getBlob(cursor.getColumnIndex(column));
        } else {
            return null;
        }
    }

    /**
     * 通过field获取游标中的数据
     */
    private static Object getValueByField(Column column, Field field, Cursor cursor) {
        Class<?> params = field.getType();
        String columnName = AnnotateSupport.getColumnName(field, column);
        if (String.class.isAssignableFrom(params)) {
            return cursor.getString(cursor.getColumnIndex(columnName));
        } else if (int.class.isAssignableFrom(params)) {
            return cursor.getInt(cursor.getColumnIndex(columnName));
        } else if (long.class.isAssignableFrom(params)) {
            return cursor.getLong(cursor.getColumnIndex(columnName));
        } else if (double.class.isAssignableFrom(params)) {
            return cursor.getDouble(cursor.getColumnIndex(columnName));
        } else if (float.class.isAssignableFrom(params)) {
            return cursor.getFloat(cursor.getColumnIndex(columnName));
        } else if (boolean.class.isAssignableFrom(params)) {
            return (cursor.getInt(cursor.getColumnIndex(columnName)) == 1);
        } else if (byte[].class.isAssignableFrom(params)) {
            return cursor.getBlob(cursor.getColumnIndex(columnName));
        } else {
            return null;
        }
    }

    public static String getColumnType(Field field, Column column) {
        Class<?> params = field.getType();
        if (!TextUtils.isEmpty(column.type())) {
            return column.type();
        } else if (String.class.isAssignableFrom(params)) {
            return "NVARCHAR";
        } else if (long.class.isAssignableFrom(params) || double.class.isAssignableFrom(params) || float.class.isAssignableFrom(params)) {
            return "REAL";
        } else if (int.class.isAssignableFrom(params)) {
            return "INTEGER";
        } else if (boolean.class.isAssignableFrom(params)) {
            return "INTEGER";
        } else if (byte[].class.isAssignableFrom(params)) {
            return "BLOB";
        } else {
            return "TEXT";
        }
    }

}
