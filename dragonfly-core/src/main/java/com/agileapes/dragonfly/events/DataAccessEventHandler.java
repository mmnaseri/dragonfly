package com.agileapes.dragonfly.events;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/9, 1:15)
 */
public interface DataAccessEventHandler {

    <E> void beforeSave(E entity);

    <E> void afterSave(E entity);

    <E> void beforeInsert(E entity);

    <E> void afterInsert(E entity);

    <E> void beforeUpdate(E entity);

    <E> void afterUpdate(E entity, boolean updated);

    <E> void beforeDelete(E entity);

    <E> void afterDelete(E entity);

    <E, K extends Serializable> void beforeDelete(Class<E> entityType, K key);

    <E, K extends Serializable> void afterDelete(Class<E> entityType, K key);

    <E> void beforeDeleteAll(Class<E> entityType);

    <E> void afterDeleteAll(Class<E> entityType);

    <E> void beforeTruncate(Class<E> entityType);

    <E> void afterTruncate(Class<E> entityType);

    <E> void beforeFind(E sample);

    <E> void afterFind(E sample, List<E> entities);

    <E, K extends Serializable> void beforeFind(Class<E> entityType, K key);

    <E, K extends Serializable> void afterFind(Class<E> entityType, K key, E entity);

    <E> void beforeFindAll(Class<E> entityType);

    <E> void afterFindAll(Class<E> entityType, List<E> entities);

    <E> void beforeExecuteUpdate(Class<E> entityType, String queryName, Map<String, Object> values);

    <E> void afterExecuteUpdate(Class<E> entityType, String queryName, Map<String, Object> values, int affectedRows);

    <E> void beforeExecuteUpdate(E sample, String queryName);

    <E> void afterExecuteUpdate(E sample, String queryName, int affectedRows);

    <E> void beforeExecuteQuery(Class<E> entityType, String queryName, Map<String, Object> values);

    <E> void afterExecuteQuery(Class<E> entityType, String queryName, Map<String, Object> values, List<E> entities);

    <E> void beforeExecuteQuery(E sample, String queryName);

    <E> void afterExecuteQuery(E sample, String queryName, List<E> entities);

}
