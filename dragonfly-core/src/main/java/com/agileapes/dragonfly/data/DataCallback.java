package com.agileapes.dragonfly.data;

import com.agileapes.couteau.basics.api.Filter;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/26, 2:40)
 */
public interface DataCallback<E extends DataOperation> extends Filter<E> {

    Object execute(E operation);

}
