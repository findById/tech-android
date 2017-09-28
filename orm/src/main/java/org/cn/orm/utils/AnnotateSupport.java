package org.cn.orm.utils;

import org.cn.orm.annotation.Column;
import org.cn.orm.annotation.Entity;
import org.cn.orm.annotation.Id;

import java.lang.reflect.Field;

public class AnnotateSupport {

    public static String getEntityName(Class<?> clazz) {
        return getEntityName(clazz, clazz.getAnnotation(Entity.class));
    }

    public static String getEntityName(Class<?> clazz, Entity entity) {
        if (entity != null) {
            return "".equals(entity.name()) || entity.name() == null ? clazz.getSimpleName() : entity.name();
        }
        throw new IllegalArgumentException(clazz.getName() + " is not entity");
    }

    public static String getIdName(Class<?> clazz) {
        String id = null;
        for (Field field : clazz.getDeclaredFields()) {
            id = getIdName(field, field.getAnnotation(Id.class));
            if (id != null) {
                return id;
            }
        }
        throw new IllegalArgumentException(clazz.getSimpleName() + " is not @Id ann");
    }

    public static Object[] getIdValue(Object object) {
        Class<?> clazz = object.getClass();
        String id = null;
        Object[] result = new Object[2];
        for (Field field : clazz.getDeclaredFields()) {
            id = getIdName(field, field.getAnnotation(Id.class));
            if (id != null) {
                result[0] = id;
                result[1] = ReflectSupport.getFieldValue(field, object);
                return result;
            }
        }
        throw new IllegalArgumentException(clazz.getSimpleName() + " is not @Id ann");
    }

    public static String getIdName(Field field) {
        return getIdName(field, field.getAnnotation(Id.class));
    }

    public static String getIdName(Field field, Id id) {
        if (id != null) {
            return (id.value() == null || id.value().length() <= 0) ? field.getName() : id.value();
        }
        return null;
    }

    public static String getColumnName(Field field) {
        return getColumnName(field, field.getAnnotation(Column.class));
    }

    public static String getColumnName(Field field, Column column) {
        if (column != null) {
            return column.name() == null || "".equals(column.name()) ? field.getName() : column.name();
        }
        throw new IllegalArgumentException(field.getName() + " is not column");
    }
}
