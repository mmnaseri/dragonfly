package com.agileapes.dragonfly.api;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/8, 13:59)
 */
public interface DataStructureHandler {

    <E> void defineTable(Class<E> entityType);

    <E> void definePrimaryKey(Class<E> entityType);

    <E> void defineSequences(Class<E> entityType);

    <E> void defineForeignKeys(Class<E> entityType);
    
    <E> void defineUniqueConstraints(Class<E> entityType);

    <E> void removeTable(Class<E> entityType);

    <E> void removePrimaryKeys(Class<E> entityType);

    <E> void removeSequences(Class<E> entityType);

    <E> void removeForeignKeys(Class<E> entityType);
    
    <E> void removeUniqueConstraints(Class<E> entityType);

    <E> void bindSequences(Class<E> entityType);

    <E> void unbindSequences(Class<E> entityType);

    <E> boolean isDefined(Class<E> entityType);

    void initialize();

}
