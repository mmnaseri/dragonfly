package com.agileapes.dragonfly.api;

import com.agileapes.dragonfly.entity.EntityContext;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/5, 19:06)
 */
public interface DataAccess extends EntityContext {

    <E> void save(E entity);

    <E> void delete(E entity);

    <E, K extends Serializable> void delete(Class<E> entityType, K key);

    <E> void deleteAll(Class<E> entityType);

    <E> List<E> find(E sample);

    <E, K extends Serializable> E find(Class<E> entityType, K key);

    <E> List<E> findAll(Class<E> entityType);

    <E, K extends Serializable> K getKey(E entity);

    <E> int executeUpdate(Class<E> entityType, String queryName, Map<String, Object> values);

    <E> int executeUpdate(E sample, String queryName);

    <E> List<E> executeQuery(Class<E> entityType, String queryName, Map<String, Object> values);

    <E> List<E> executeQuery(E sample, String queryName);

}
