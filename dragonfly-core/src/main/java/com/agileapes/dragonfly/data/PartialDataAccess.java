package com.agileapes.dragonfly.data;

import com.agileapes.dragonfly.annotations.Partial;

import java.util.List;
import java.util.Map;

/**
 * <p>This interface extends the functionalities provided by {@link DataAccess} to enable
 * working with complex entities that are essentially non-persistent, but are defined
 * as a convenience for working better with data values.</p>
 *
 * <p>Do note that using partial objects as a recurring phenomena in code is not advisable
 * and invites all sorts of design flaws. Therefore, if you are relying heavily on methods
 * from this interface, your code should undergo serious refactoring. That is one reason
 * all <em>partial</em> methods have been removed from the {@link DataAccess} interface
 * itself and moved here.</p>
 *
 * @see Partial
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/7, 13:21)
 */
public interface PartialDataAccess extends DataAccess {

    /**
     * This method will execute the query specified using the metadata from the {@link Partial}
     * annotation on the given sample object's class, and then use the object itself to provide
     * data to the query.
     * @param sample    the sample object
     * @param <O>       the type of the object
     * @return list of matching items
     */
    <O> List<O> executeQuery(O sample);

    /**
     * This method will execute the query specified using the metadata from the {@link Partial}
     * annotation on the given result type class.
     * @param resultType    the type of the object to be used for querying
     * @param <O>           the type of result values
     * @return list of matching items
     */
    <O> List<O> executeQuery(Class<O> resultType);

    /**
     * This method will execute the query specified using the metadata from the {@link Partial}
     * annotation on the given result type class.
     * @param resultType    the type of the object to be used for querying
     * @param values        the values to be used for query model injection
     * @param <O>           the type of result values
     * @return list of matching items
     */
    <O> List<O> executeQuery(Class<O> resultType, Map<String, Object> values);

    /**
     * This method will execute the given named query from the given entity type, and then
     * attempt to return key-value maps of the query's result by injecting values from the
     * provided map into the named query where necessary.
     * @param entityType    the type of the entity. This must be an entity for which
     *                      table metadata has been previously decided, or can be
     *                      readily determined.
     * @param queryName     the name of the query to be executed. This query has to be
     *                      of a value-returning (query) type.
     * @param values        the map of values
     * @param <E>           type of the entity
     * @return a list of all matching items.
     */
    <E> List<Map<String, Object>> executeUntypedQuery(Class<E> entityType, String queryName, Map<String, Object> values);

    /**
     * This method will run an update (non-returning) query on the database using the values
     * provided via the sample partial entity. The partial entity must specify the target
     * query using the {@link Partial} annotation on its declaring class.
     * @param sample    the sample object
     * @param <O>       the type of input
     * @return number of items affected by the query
     */
    <O> int executeUpdate(O sample);

}
