package com.agileapes.dragonfly.metadata.impl;

import com.agileapes.dragonfly.metadata.OrderMetadata;
import com.agileapes.dragonfly.metadata.ResultOrderMetadata;

import java.util.*;

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
}
