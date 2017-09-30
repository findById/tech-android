package org.cn.orm.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflectSupport {

    /**
     * 通过getter获取对象值
     */
    public static void getMethodValues(Object object) {
        Class<?> clazz = object.getClass();
        String entityName = "";
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            String methodName = method.getName();
            if ((methodName.startsWith("get") || methodName.startsWith("is")) && !methodName.startsWith("getClass")) {
                Object value = getMethodValue(method, object);
            }
        }
    }

    /**
     * 通过public字段直接获取对象值
     */
    public static void getFieldValues(Object object) {
        Class<?> clazz = object.getClass();
        String entityName = "";
        Field[] fields = clazz.getFields();
        for (Field field : fields) {
            Object value = getFieldValue(field, object);
        }
    }

    /**
     * 通过public (field)字段直接获取值
     */
    public static Object getFieldValue(Field field, Object object) {
        try {
            Object value = field.get(object);
            if (value instanceof Boolean) {
                return value.equals(true) ? 1 : 0;
            }
            return value;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 通过method (getter)获取对象值
     */
    public static Object getMethodValue(Method method, Object object) {
        try {
            Object value = method.invoke(object);
            if (value instanceof Boolean) {
                return value.equals(true) ? 1 : 0;
            } else if (value instanceof String) {
                return "'" + value + "'";
            }
            return value;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 通过setter赋值
     */
    public static Object invokeMethod(Class<?> clazz) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Object object = clazz.newInstance();
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            String methodName = method.getName();
            if (methodName.startsWith("set")) {
                String column = methodName.substring(3, methodName.length());
                method.invoke(object, "");
            }
        }
        return object;
    }

    /**
     * 通过public字段直接赋值
     */
    public static Object invokeField(Class<?> clazz) throws InstantiationException, IllegalAccessException {
        Object object = clazz.newInstance();
        Field[] fields = clazz.getFields();
        for (Field field : fields) {
            field.set(object, "");
        }
        return object;
    }

}
