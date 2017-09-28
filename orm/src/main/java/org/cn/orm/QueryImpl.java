package org.cn.orm;

import android.database.Cursor;
import android.text.TextUtils;

import org.cn.orm.utils.AnnotateSupport;
import org.cn.orm.utils.SQLiteUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class QueryImpl implements Query {

    private SQLiteHelper helper;
    private HashMap<String, Object> parameters = new HashMap<>();
    private Class<?> entity = null;
    private int firstResult = 0;
    private int maxResults = 0;

    private StringBuffer query = new StringBuffer();

    public QueryImpl(SQLiteHelper helper) {
        this(helper, null);
    }

    public QueryImpl(SQLiteHelper helper, String query) {
        this.helper = helper;
        if (!TextUtils.isEmpty(query)) {
            this.query.setLength(0);
            this.query.append(query);
        }
    }

    @Override
    public Query addEntity(Class<?> clazz) {
        if (TextUtils.isEmpty(query)) {
            query.append("SELECT * FROM " + AnnotateSupport.getEntityName(clazz));
        }
        this.entity = clazz;
        return this;
    }

    @Override
    public Query setParameter(String key, Object value) {
        parameters.put(key, value);
        return this;
    }

    @Override
    public Query setFirstResult(int firstResult) {
        this.firstResult = firstResult;
        return this;
    }

    @Override
    public Query setMaxResults(int maxResults) {
        this.maxResults = maxResults;
        return this;
    }

    @Override
    public int executeUpdate() {
        return helper.executeUpdate(query.toString());
    }

    @Override
    public List list() {
        if (maxResults > 0 && !query.toString().toUpperCase().contains("LIMIT ")) {
            query.append(" LIMIT ").append(firstResult).append(",").append(maxResults);
        }
        List list = new ArrayList();
        try {
            Cursor cursor = helper.executeQuery(query.toString());
            if (cursor.getCount() <= 0) {
                return list;
            }
            cursor.moveToFirst();
            do {
                list.add(SQLiteUtil.invokeField(entity, cursor));
            } while (cursor.moveToNext());
        } catch (Throwable ignored) {
        }
        return list;
    }

    @Override
    public Object uniqueResult() {
        Cursor cursor = helper.executeQuery(query.toString());
        cursor.moveToFirst();
        return SQLiteUtil.invokeField(entity, cursor);
    }
}
