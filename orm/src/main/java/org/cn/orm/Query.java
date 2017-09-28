package org.cn.orm;

import java.util.List;

public interface Query {

    Query addEntity(Class<?> clazz);

    Query setParameter(String key, Object value);

    Query setFirstResult(int firstResult);

    Query setMaxResults(int maxResults);

    int executeUpdate();

    List list();

    Object uniqueResult();

}
