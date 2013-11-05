package com.agileapes.dragonfly.data;

import com.agileapes.couteau.basics.api.Filter;

/**
 * This interface allows for definition of callbacks that will be used to handle
 * operations through the data access interface
 *
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/26, 2:40)
 */
public interface DataCallback<E extends DataOperation> extends Filter<E> {

    /**
     * <p>This method is called to indicate an action is taking place through the
     * data access interface.</p>
     * <p>Note that you should call to {@link DataOperation#proceed()} manually inside the
     * body of your {@code execute(DataOperation)} method to indicate that the operation
     * should be carried out as pre-designated.</p>
     * @param operation    the operation being performed
     * @return the result of the operation
     */
    Object execute(E operation);

}
