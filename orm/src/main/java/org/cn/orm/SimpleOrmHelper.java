package org.cn.orm;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.cn.orm.utils.AnnotateSupport;
import org.cn.orm.utils.SQLUtil;
import org.cn.orm.utils.SQLiteUtil;

import java.util.ArrayList;
import java.util.Arrays;

public class SimpleOrmHelper implements OrmHelper {

    private SQLiteHelper helper;

    public SimpleOrmHelper(SQLiteHelper helper) {
        this.helper = helper;
    }

    public SQLiteDatabase getCurrentDatabase(boolean readonly) {
        return helper.getDatabase(readonly);
    }

    @Override
    public Query createQuery() {
        return new QueryImpl(helper);
    }

    @Override
    public Query createQuery(String query) {
        return new QueryImpl(helper, query);
    }

    @Override
    public Object find(Class<?> entity, Object primaryKey) {
        String sql = SQLUtil.findById(entity);
        Cursor cursor = helper.executeQuery(sql, String.valueOf(primaryKey));
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            return SQLiteUtil.invokeField(entity, cursor);
        }
        return null;
    }

    @Override
    public void persist(Object entity) {
        helper.executeUpdate(SQLUtil.insert(entity), SQLUtil.getValues(entity));
    }

    @Override
    public void update(Object entity) {
        String sql = SQLUtil.update(entity);
        Object[] temp = SQLUtil.getUpdateValues(entity);
        Object[] values = Arrays.copyOf(temp, temp.length + 1);
        values[temp.length] = AnnotateSupport.getIdValue(entity)[1];
        helper.executeUpdate(sql, values);
    }

    @Override
    public void remove(Object entity) {
        String sql = SQLUtil.delete(entity);
        helper.executeUpdate(sql, AnnotateSupport.getIdValue(entity)[1]);
    }

    public void saveAll(Object... objects) {
        helper.beginTransaction();
        for (Object entity : objects) {
            persist(entity);
        }
        helper.commit();
    }

    public void saveOrUpdate(Object entity) {
        if (find(entity.getClass(), AnnotateSupport.getIdValue(entity)[1]) != null) {
            update(entity);
        } else {
            persist(entity);
        }
    }

    public void saveOrUpdateAll(Object... objects) {
        helper.beginTransaction();
        for (Object entity : objects) {
            saveOrUpdate(entity);
        }
        helper.commit();
    }

    public static Builder create() {
        return new Builder();
    }

    public static class Builder {

        private ArrayList<Class<?>> classes = new ArrayList<>();

        public Builder addAnnotatedClass(Class<?> clazz) {
            classes.add(clazz);
            return this;
        }

        public SimpleOrmHelper build(Context ctx, String name, int version) {
            SQLiteHelper helper = new SQLiteHelper(ctx, name, null, version);
            for (Class<?> clazz : classes) {
                String sql = SQLUtil.createTable(clazz);
                Log.d("ORM", "" + sql);
                helper.executeUpdate(sql);
            }
            return new SimpleOrmHelper(helper);
        }
    }
}
