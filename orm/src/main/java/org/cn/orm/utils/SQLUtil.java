package org.cn.orm.utils;

import org.cn.orm.annotation.Column;
import org.cn.orm.annotation.Id;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SQLUtil {
    public static final String UPDATE_FLAG = "update.flag";
    public static final String SAVE_FLAG = "save.flag";
    public static final String DELETE_FLAG = "delete.flag";

    public static String createTable(Class<?> clazz) {
        StringBuffer sql = new StringBuffer();
        sql.append("CREATE TABLE IF NOT EXISTS ");
        sql.append(AnnotateSupport.getEntityName(clazz));
        sql.append("(");
        for (Field field : clazz.getDeclaredFields()) {
            Column column = field.getAnnotation(Column.class);
            if (column != null) {
                sql.append(AnnotateSupport.getColumnName(field, column));
                sql.append(" ");
                sql.append(SQLiteUtil.getColumnType(field));
                sql.append("(");
                sql.append(column.length());
                sql.append(")");
                sql.append(column.nullable() ? " NULL" : " NOT NULL");
            }
            Id id = field.getAnnotation(Id.class);
            if (id != null) {
                sql.append(" PRIMARY KEY");
            }
            if (id != null || column != null) {
                sql.append(",");
            }
        }
        sql.replace(sql.length() - 1, sql.length(), ")");
        return sql.toString();
    }

    public static String findById(Class<?> clazz) {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT * FROM ");
        sql.append(AnnotateSupport.getEntityName(clazz));
        sql.append(" WHERE ").append(AnnotateSupport.getIdName(clazz));
        sql.append("=?");
        return sql.toString();
    }

    public static String save(Object object) {
        Class<?> clazz = object.getClass();
        String cached = SQLCache.get(SQLUtil.SAVE_FLAG + AnnotateSupport.getEntityName(clazz));

        if (cached != null && cached.length() > 0) {
            return cached;
        }

        StringBuffer sql = new StringBuffer();
        sql.append("INSERT INTO ");
        sql.append(AnnotateSupport.getEntityName(clazz));
        sql.append("(");

        StringBuffer value = new StringBuffer();
        value.append("VALUES(");

        for (Field field : clazz.getDeclaredFields()) {
            if (field.getAnnotation(Column.class) != null) {
                sql.append(AnnotateSupport.getColumnName(field)).append(",");
                value.append("?").append(",");
            }
        }

        sql.replace(sql.length() - 1, sql.length(), ")");
        value.replace(value.length() - 1, value.length(), ")");
        sql.append(value);
        SQLCache.put(SQLUtil.SAVE_FLAG + AnnotateSupport.getEntityName(clazz), sql.toString());
        return sql.toString();
    }

    public static String update(Object object) {
        Class<?> clazz = object.getClass();

        String cached = SQLCache.get(SQLUtil.UPDATE_FLAG + AnnotateSupport.getEntityName(clazz));

        if (cached != null && cached.length() > 0) {
            return cached;
        }

        StringBuffer sql = new StringBuffer();
        sql.append("UPDATE ");
        sql.append(AnnotateSupport.getEntityName(clazz));
        sql.append(" SET ");

        for (Field field : clazz.getDeclaredFields()) {
            if (field.getAnnotation(Column.class) != null) {
                sql.append(AnnotateSupport.getColumnName(field)).append("=?,");
            }
        }

        sql.replace(sql.length() - 1, sql.length(), "");
//        Object[] obj = AnnotateSupport.getIdValue(object);
//        sql.append(" WHERE ").append(String.valueOf(obj[0])).append("=").append(obj[1]);
        sql.append(" WHERE ").append("?").append("=").append("?");

        SQLCache.put(SQLUtil.UPDATE_FLAG + AnnotateSupport.getEntityName(clazz), sql.toString());
        return sql.toString();
    }

    public static String delete(Object object) {
        Class<?> clazz = object.getClass();
        String cached = SQLCache.get(SQLUtil.DELETE_FLAG + AnnotateSupport.getEntityName(clazz));

        if (cached != null && cached.length() > 0) {
            return cached;
        }

        StringBuffer sql = new StringBuffer("DELETE FROM ");
        sql.append(AnnotateSupport.getEntityName(clazz));
        sql.append(" WHERE ");
        String id = AnnotateSupport.getIdName(clazz);
        sql.append(id).append("=?");

        SQLCache.put(SQLUtil.DELETE_FLAG + AnnotateSupport.getEntityName(clazz), sql.toString());
        return sql.toString();
    }

    public static String add(Object object) {
        // 新增字段: ALTER TABLE [表名] ADD [字段名] NVARCHAR (50) NULL，[字段名] NVARCHAR (50) NULL
        Class<?> clazz = object.getClass();

        StringBuffer sql = new StringBuffer("ALTER TABLE ");
        sql.append(AnnotateSupport.getEntityName(clazz));
        sql.append(" ADD ");

        sql.append("");

        return sql.toString();
    }

    public static String drop(Object object) {
        // 删除字段: ALTER TABLE [表名] DROP COLUMN [字段名]
        Class<?> clazz = object.getClass();

        StringBuffer sql = new StringBuffer("ALTER TABLE ");
        sql.append(AnnotateSupport.getEntityName(clazz));
        sql.append(" DROP ");

        sql.append("COLUMN").append("").append(",");

        return sql.toString();
    }

    public static Object[] getValues(Object object) {
        Class<?> clazz = object.getClass();
        List<Object> list = new ArrayList<>();

        for (Field field : clazz.getDeclaredFields()) {
            if (field != null && field.getAnnotation(Column.class) != null) {
                list.add(ReflectSupport.getFieldValue(field, object));
            }
        }

        return list.toArray(new Object[list.size()]);
    }

    public static Object invokeMethod(Class<?> clazz, ResultSet result) {
        Object object = null;
        try {
            object = clazz.newInstance();
            Method[] methods = clazz.getMethods();
            for (Method method : methods) {
                if (method.getName().startsWith("set")) {
                    method.invoke(object, getValueByMethod(method, result));
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

    public static Object invokeField(Class<?> clazz, ResultSet result) {
        Object object = null;
        try {
            object = clazz.newInstance();
            Field[] fields = clazz.getFields();
            for (Field field : fields) {
                Column column = field.getAnnotation(Column.class);
                if (column != null) {
                    field.set(object, getValueByField(column, field, result));
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
     *
     * @param method
     * @param result
     * @return
     */
    private static Object getValueByMethod(Method method, ResultSet result) {
        Class<?> params = method.getParameterTypes()[0];
        String column = method.getName().substring(3, method.getName().length());
        column = new StringBuffer(column).replace(0, 1, column.toLowerCase().substring(0, 1)).toString();
        try {
            if (String.class.isAssignableFrom(params)) {
                return result.getString(result.findColumn(column));
            } else if (int.class.isAssignableFrom(params)) {
                return result.getInt(result.findColumn(column));
            } else if (long.class.isAssignableFrom(params)) {
                return result.getLong(result.findColumn(column));
            } else if (double.class.isAssignableFrom(params)) {
                return result.getDouble(result.findColumn(column));
            } else if (float.class.isAssignableFrom(params)) {
                return result.getFloat(result.findColumn(column));
            } else if (boolean.class.isAssignableFrom(params)) {
                return (result.getInt(result.findColumn(column)) == 1);
            } else if (byte[].class.isAssignableFrom(params)) {
                return result.getBlob(result.findColumn(column));
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 通过field获取游标中的数据
     *
     * @param field
     * @param result
     * @return
     */
    private static Object getValueByField(Column column, Field field, ResultSet result) {
        Class<?> params = field.getType();
        String columnName = AnnotateSupport.getColumnName(field, column);
        try {
            if (String.class.isAssignableFrom(params)) {
                return result.getString(result.findColumn(columnName));
            } else if (int.class.isAssignableFrom(params)) {
                return result.getInt(result.findColumn(columnName));
            } else if (long.class.isAssignableFrom(params)) {
                return result.getLong(result.findColumn(columnName));
            } else if (double.class.isAssignableFrom(params)) {
                return result.getDouble(result.findColumn(columnName));
            } else if (float.class.isAssignableFrom(params)) {
                return result.getFloat(result.findColumn(columnName));
            } else if (boolean.class.isAssignableFrom(params)) {
                return (result.getInt(result.findColumn(columnName)) == 1);
            } else if (byte[].class.isAssignableFrom(params)) {
                return result.getBlob(result.findColumn(columnName));
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getColumnType(Field field) {
        Class<?> params = field.getType();
        if (String.class.isAssignableFrom(params)) {
            return "VARCHAR";
        } else if (int.class.isAssignableFrom(params)) {
            return "INTEGER";
        } else if (long.class.isAssignableFrom(params)) {
            return "VARCHAR";
        } else if (double.class.isAssignableFrom(params)) {
            return "VARCHAR";
        } else if (float.class.isAssignableFrom(params)) {
            return "VARCHAR";
        } else if (boolean.class.isAssignableFrom(params)) {
            return "INTEGER";
        } else if (byte[].class.isAssignableFrom(params)) {
            return "BLOB";
        } else {
            return "TEXT";
        }
    }

}
