package org.cn.orm;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.cn.orm.utils.AnnotateSupport;
import org.cn.orm.utils.SQLUtil;
import org.cn.orm.utils.SQLiteUtil;

import java.io.Serializable;
import java.util.ArrayList;

public class SimpleOrmHelper implements OrmHelper {

    private SQLiteHelper helper;

    public SimpleOrmHelper(SQLiteHelper helper) {
        this.helper = helper;
    }

    public SQLiteDatabase getDatabase(boolean readonly) {
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
    public Object get(Class<?> clazz, Serializable id) {
        String sql = SQLUtil.findById(clazz);
        Cursor cursor = helper.executeQuery(sql, String.valueOf(id));
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            return SQLiteUtil.invokeField(clazz, cursor);
        }
        return null;
    }

    @Override
    public void save(Object object) {
        String sql = SQLUtil.save(object);
        helper.executeUpdate(sql, SQLUtil.getValues(object));
    }

    @Override
    public void update(Object object) {
        String sql = SQLUtil.update(object);
        helper.executeUpdate(sql, SQLUtil.getValues(object), AnnotateSupport.getIdValue(object));
    }

    @Override
    public void delete(Object object) {
        String sql = SQLUtil.delete(object);
        helper.executeUpdate(sql, AnnotateSupport.getIdValue(object)[1]);
    }

    public void saveAll(Object... objects) {
        helper.beginTransaction();
        for (Object obj : objects) {
            helper.executeUpdate(SQLUtil.save(obj), SQLUtil.getValues(obj));
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
