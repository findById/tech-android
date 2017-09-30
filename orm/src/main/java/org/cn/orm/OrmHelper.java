package org.cn.orm;

public interface OrmHelper {
    Query createQuery();

    Query createQuery(String query);

    Object find(Class<?> entity, Object primaryKey);

    void persist(Object entity);

    void update(Object entity);

    void remove(Object entity);
}
