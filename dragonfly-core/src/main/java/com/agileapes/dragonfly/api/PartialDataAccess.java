package com.agileapes.dragonfly.api;

import java.util.List;
import java.util.Map;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/7, 13:21)
 */
public interface PartialDataAccess extends DataAccess {

    <O> List<O> executePartialQuery(O sample);

    <O> List<O> executePartialQuery(Class<O> resultType);

    <O> List<O> executePartialQuery(Class<O> resultType, Map<String, Object> values);

    <E, O> List<O> executePartialQuery(Class<E> entityType, String queryName, Class<O> resultType, Map<String, Object> values);

    <E> List<Map<String, Object>> executePartialQuery(Class<E> entityType, String queryName, Map<String, Object> values);

    <O> int executePartialUpdate(O sample);

    <E> int executePartialUpdate(Class<E> entityType, String queryName, Map<String, Object> values);

    <E> int executePartialUpdate(Class<E> entityType, String queryName);

}
