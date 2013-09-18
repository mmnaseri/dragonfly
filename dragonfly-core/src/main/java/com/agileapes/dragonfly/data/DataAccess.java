package com.agileapes.dragonfly.data;

import com.agileapes.dragonfly.entity.EntityContext;
import com.agileapes.dragonfly.metadata.TableMetadata;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * <p>This interface exposes the main functionalities expected from a data access layer
 * interface.</p>
 *
 * <p>This is essentially the core API at the heart of this framework, which enables
 * users to access the database seamlessly and without direct contact.</p>
 *
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/5, 19:06)
 */
public interface DataAccess {

    /**
     * <p>Attempts to persist the given entity. This is done either by inserting the new
     * entity into the database, or by updating an existing entity.</p>
     *
     * <p>An update is triggered if and only if exactly one entity sharing the state of
     * the given entity is found. For entities with a key, this can be simplified to sharing
     * the key value.</p>
     * @param entity    the entity to be persisted. This entity must belong to the data access
     *                  interface's defining context, i.e., it must be initialized through
     *                  {@link EntityContext#getInstance(Class)} or {@link EntityContext#getInstance(TableMetadata)}
     * @param <E>       the type of the entity
     */
    <E> void save(E entity);

    /**
     * <p>Attempts to delete the given entity. This is done by taking the given
     * entity as a sample. If it can be properly identified with a primary key,
     * then only one item will be deleted. If not, however, <em>any</em> item matching the
     * item at hand will be deleted.</p>
     * @param entity    the entity to be deleted. This entity must belong to the data access
     *                  interface's defining context, i.e., it must be initialized through
     *                  {@link EntityContext#getInstance(Class)} or {@link EntityContext#getInstance(TableMetadata)}
     * @param <E>       the type of the entity
     */
    <E> void delete(E entity);

    /**
     * Attempts to delete an item of the given type, with the given key.
     * @param entityType    the type of the entity. This must be an entity for which
     *                      table metadata has been previously decided, or can be
     *                      readily determined.
     * @param key           the key to the item.
     * @param <E>           the type of the entity.
     * @param <K>           the type of the key.
     */
    <E, K extends Serializable> void delete(Class<E> entityType, K key);

    /**
     * Deletes all items in the table associated with the entity type.
     * @param entityType    the type of the entity. This must be an entity for which
     *                      table metadata has been previously decided, or can be
     *                      readily determined.
     * @param <E>           the type of the entity.
     */
    <E> void deleteAll(Class<E> entityType);

    /**
     * Empties the items in the table from the given type. <strong>Be warned</strong> that in most implementations
     * of the different database engines available, while this is (probably much) faster than calling to {@link #deleteAll(Class)},
     * it is also not undoable and in the context of a transaction cannot be rolled back.
     * @param entityType    the type of the entity. This must be an entity for which
     *                      table metadata has been previously decided, or can be
     *                      readily determined.
     * @param <E>           the type of the entity.
     */
    <E> void truncate(Class<E> entityType);

    /**
     * Finds all matching items in the database.
     * @param sample    the entity to be used as a sample. This entity must belong to the data access
     *                  interface's defining context, i.e., it must be initialized through
     *                  {@link EntityContext#getInstance(Class)} or {@link EntityContext#getInstance(TableMetadata)}
     * @param <E>       the type of the entity.
     * @return a list of all items matching the description as provided by the sample.
     */
    <E> List<E> find(E sample);

    /**
     * Attempts to find a single entry in the database matching the given type and having
     * the designated key.
     * @param entityType    the type of the entity. This must be an entity for which
     *                      table metadata has been previously decided, or can be
     *                      readily determined.
     * @param key           the key to the entity
     * @param <E>           the type of the entity
     * @param <K>           the type of the key
     * @return the item with the given key
     */
    <E, K extends Serializable> E find(Class<E> entityType, K key);

    /**
     * Attempts to list all items with the given type registered with the database accessible
     * to the current session
     * @param entityType    the type of the entity to be enlisted
     * @param <E>           the type of the entity
     * @return a list of all available items
     */
    <E> List<E> findAll(Class<E> entityType);

    /**
     * Returns the value of the key attributed to the entity
     * @param entity    the entity to be examined. This entity must belong to the data access
     *                  interface's defining context, i.e., it must be initialized through
     *                  {@link EntityContext#getInstance(Class)} or {@link EntityContext#getInstance(TableMetadata)}
     * @param <E>       the type of the entity
     * @param <K>       the type of the key
     * @return the value of the key
     */
    <E, K extends Serializable> K getKey(E entity);

    /**
     * Executes the named query for the item, injecting values as necessary from the
     * provided map
     * @param entityType    the type of the entity. This must be an entity for which
     *                      table metadata has been previously decided, or can be
     *                      readily determined.
     * @param queryName     the name of the query to be executed. This query has to be
     *                      of a non-returning (update) type.
     * @param values        the values to be used while processing the query.
     * @param <E>           the type of the entity.
     * @return the number of items affected by this query.
     */
    <E> int executeUpdate(Class<E> entityType, String queryName, Map<String, Object> values);

    /**
     * Executes the named query for the item, injecting values as necessary from the
     * provided sample
     * @param sample        the entity to be used as a sample. This entity must belong to the data access
     *                      interface's defining context, i.e., it must be initialized through
     *                      {@link EntityContext#getInstance(Class)} or {@link EntityContext#getInstance(TableMetadata)}
     * @param queryName     the name of the query to be executed. This query has to be
     *                      of a non-returning (update) type.
     * @param <E>           the type of the entity.
     * @return the number of items affected by this query.
     */
    <E> int executeUpdate(E sample, String queryName);

    /**
     * Executes the query for the given type, using the query named and injecting values
     * into the query with help from the specified map of values as necessary.
     * @param entityType    the type of the entity. This must be an entity for which
     *                      table metadata has been previously decided, or can be
     *                      readily determined.
     * @param queryName     the name of the query to be executed. This query has to be
     *                      of a value-returning (query) type.
     * @param values        the map of values to be used for value injection
     * @param <E>           the type of the items
     * @return the list of items matching the criteria of the query
     */
    <E> List<E> executeQuery(Class<E> entityType, String queryName, Map<String, Object> values);

    /**
     * Executes the query for the given type, using the query named and injecting values
     * into the query with help from the specified sample as necessary.
     * @param sample        the entity to be used as a sample. This entity must belong to the data access
     *                      interface's defining context, i.e., it must be initialized through
     *                      {@link EntityContext#getInstance(Class)} or {@link EntityContext#getInstance(TableMetadata)}
     * @param queryName     the name of the query to be executed. This query has to be
     *                      of a value-returning (query) type.
     * @param <E>           the type of the items
     * @return the list of items matching the criteria of the query
     */
    <E> List<E> executeQuery(E sample, String queryName);

    /**
     * Executes the given procedure on the server side, returning any generated results
     * @param entityType       the type of the entity for which the procedure is defined
     * @param procedureName    the name of the procedure
     * @param parameters       the parameters to the procedure. Note that parameters corresponding
     *                         to {@link com.agileapes.dragonfly.annotations.ParameterMode#OUT} and
     *                         {@link com.agileapes.dragonfly.annotations.ParameterMode#IN_OUT} must
     *                         be of type {@link Reference} with the generics type specified to be
     *                         matching that of the defined parameter
     * @param <E>              the type of the entity over which the call is taking place
     * @return the result of the call of an empty list if no result is returned
     */
    <E> List<?> call(Class<E> entityType, String procedureName, Object... parameters);

}
