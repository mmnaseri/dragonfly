/*
 * Copyright (c) 2013 AgileApes, Ltd.
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall
 * be included in all copies or substantial portions of the
 * Software.
 */

package com.agileapes.dragonfly.metadata.impl;

import com.agileapes.couteau.basics.api.Transformer;
import com.agileapes.dragonfly.metadata.OrderMetadata;
import com.agileapes.dragonfly.metadata.ResultOrderMetadata;

import java.util.*;

import static com.agileapes.couteau.basics.collections.CollectionWrapper.with;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/10/31, 9:51)
 */
//@SuppressWarnings("NullableProblems")
@SuppressWarnings("NullableProblems")
public class DefaultResultOrderMetadata implements ResultOrderMetadata {

    private final List<OrderMetadata> ordering = new ArrayList<OrderMetadata>();

    public DefaultResultOrderMetadata() {
        this(Collections.<OrderMetadata>emptyList());
    }

    public DefaultResultOrderMetadata(Collection<OrderMetadata> ordering) {
        this.ordering.addAll(ordering);
    }

    @Override
    public int size() {
        return ordering.size();
    }

    @Override
    public boolean isEmpty() {
        return ordering.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return ordering.contains(o);
    }

    @Override
    public Iterator<OrderMetadata> iterator() {
        return ordering.iterator();
    }

    @Override
    public Object[] toArray() {
        return ordering.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        //noinspection SuspiciousToArrayCall
        return ordering.toArray(a);
    }

    @Override
    public boolean add(OrderMetadata orderMetadata) {
        return ordering.add(orderMetadata);
    }

    @Override
    public boolean remove(Object o) {
        return ordering.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return ordering.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends OrderMetadata> c) {
        return ordering.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends OrderMetadata> c) {
        return ordering.addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return ordering.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return ordering.retainAll(c);
    }

    @Override
    public void clear() {
        ordering.clear();
    }

    @Override
    public int hashCode() {
        return ordering.hashCode();
    }

    @Override
    public OrderMetadata get(int index) {
        return ordering.get(index);
    }

    @Override
    public OrderMetadata set(int index, OrderMetadata element) {
        return ordering.set(index, element);
    }

    @Override
    public void add(int index, OrderMetadata element) {
        ordering.add(index, element);
    }

    @Override
    public OrderMetadata remove(int index) {
        return ordering.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return ordering.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return ordering.lastIndexOf(o);
    }

    @Override
    public ListIterator<OrderMetadata> listIterator() {
        return ordering.listIterator();
    }

    @Override
    public ListIterator<OrderMetadata> listIterator(int index) {
        return ordering.listIterator(index);
    }

    @Override
    public List<OrderMetadata> subList(int fromIndex, int toIndex) {
        return ordering.subList(fromIndex, toIndex);
    }

    @Override
    public String toString() {
        return with(ordering).transform(new Transformer<OrderMetadata, String>() {
            @Override
            public String map(OrderMetadata input) {
                return input.getColumn().getName() + " " + input.getOrder();
            }
        }).join(", ");
    }

}
