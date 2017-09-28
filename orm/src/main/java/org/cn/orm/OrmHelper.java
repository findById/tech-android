package org.cn.orm;

import java.io.Serializable;

public interface OrmHelper {
    Query createQuery();

    Query createQuery(String query);

    Object get(Class<?> clazz, Serializable id);

    void save(Object object);

    void update(Object object);

    void delete(Object object);
}
