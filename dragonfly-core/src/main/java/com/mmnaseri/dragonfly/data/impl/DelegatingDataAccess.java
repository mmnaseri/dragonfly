/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2013 Milad Naseri.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.mmnaseri.dragonfly.data.impl;

import com.mmnaseri.dragonfly.events.DataAccessEventHandler;
import com.mmnaseri.dragonfly.events.EventHandlerContext;
import com.mmnaseri.dragonfly.fluent.SelectQueryInitiator;
import com.mmnaseri.couteau.basics.api.impl.MirrorFilter;
import com.mmnaseri.couteau.basics.collections.CollectionWrapper;
import com.mmnaseri.couteau.context.impl.OrderedBeanComparator;
import com.mmnaseri.dragonfly.data.*;
import com.mmnaseri.dragonfly.data.impl.op.*;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.mmnaseri.couteau.basics.collections.CollectionWrapper.with;

/**
 * <p>This is a data access implementation that will introduce <em>around advices</em> to
 * operations of a normal data access.</p>
 *
 * <p>This is done through the introduction of {@link DataCallback} implementations by
 * adding them via {@link #addCallback(DataCallback)}. This means callbacks can be introduced
 * to add around advises for many different purposes, ranging from enabling data operations on
 * unconventional entities, to logging the duration of data access operations.</p>
 *
 * @author Milad Naseri (mmnaseri@programmer.net)
 * @since 1.0 (2013/9/26, 2:24)
 */
@SuppressWarnings("unchecked")
public class DelegatingDataAccess implements PartialDataAccess, EventHandlerContext, FluentDataAccess {

    public static final NoOpCallback DEFAULT_CALLBACK = new NoOpCallback();

    private static class NestedCallback implements DataCallback<DataOperation> {

        private final List<DataCallback<DataOperation>> callbacks;
        private final DataCallback<DataOperation> originalCallback;

        private NestedCallback(List<DataCallback<DataOperation>> callbacks, DataCallback<DataOperation> originalCallback) {
            this.callbacks = callbacks;
            this.originalCallback = originalCallback;
        }

        @Override
        public Object execute(DataOperation operation) {
            final CollectionWrapper<DataCallback<DataOperation>> wrapper = with(callbacks);
            if (wrapper.isEmpty()) {
                ((AbstractDataOperation) operation).setCallback(originalCallback);
                return operation.proceed();
            }
            ((AbstractDataOperation) operation).setCallback(new NestedCallback(wrapper.rest().list(), originalCallback));
            return wrapper.first().execute(operation);
        }

        @Override
        public boolean accepts(DataOperation dataOperation) {
            return true;
        }
    }

    private static class Executable<E extends DataOperation> {

        private final E dataOperation;
        private final DataCallback<DataOperation> callback;

        private Executable(E dataOperation, List<DataCallback<DataOperation>> callbacks) {
            this.dataOperation = dataOperation;
            this.callback = new NestedCallback(callbacks, ((AbstractDataOperation) dataOperation).getCallback());
        }

        private Object execute() {
            return callback.execute(dataOperation);
        }

    }

    private List<DataCallback<DataOperation>> callbacks = new CopyOnWriteArrayList<DataCallback<DataOperation>>();
    private final DataAccess dataAccess;

    public DelegatingDataAccess(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public synchronized void addCallback(DataCallback<?> callback) {
        callbacks = with(callbacks).add(new SmartDataCallback<DataOperation>((DataCallback<DataOperation>) callback)).sort(new OrderedBeanComparator()).concurrentList();
    }

    @Override
    public void addHandler(DataAccessEventHandler eventHandler) {
        if (dataAccess instanceof EventHandlerContext) {
            ((EventHandlerContext) dataAccess).addHandler(eventHandler);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    private <E extends DataOperation> Executable<E> given(E operation) {
        final List<DataCallback<DataOperation>> callbacks = with(this.callbacks).keep(new MirrorFilter<DataOperation>(operation)).sort(new OrderedBeanComparator()).list();
        if (callbacks.isEmpty()) {
            callbacks.add(DEFAULT_CALLBACK);
        }
        return new Executable<E>(operation, callbacks);
    }

    @Override
    public <E> E save(E entity) {
        return (E) given(new SampledDataOperation(dataAccess, OperationType.SAVE, entity, new AbstractDefaultDataCallback<SampledDataOperation>() {
            @Override
            public Object execute(SampledDataOperation operation) {
                return dataAccess.save(operation.getSample());
            }
        })).execute();
    }

    @Override
    public <E> E insert(E entity) {
        return (E) given(new SampledDataOperation(dataAccess, OperationType.INSERT, entity, new AbstractDefaultDataCallback<SampledDataOperation>() {
            @Override
            public Object execute(SampledDataOperation operation) {
                return dataAccess.insert(operation.getSample());
            }
        })).execute();
    }

    @Override
    public <E> E update(E entity) {
        return (E) given(new SampledDataOperation(dataAccess, OperationType.UPDATE, entity, new AbstractDefaultDataCallback<SampledDataOperation>() {
            @Override
            public Object execute(SampledDataOperation operation) {
                return dataAccess.update(operation.getSample());
            }
        })).execute();
    }

    @Override
    public <E> void delete(E entity) {
        given(new SampledDataOperation(dataAccess, OperationType.DELETE, entity, new AbstractProceduralDataCallback<SampledDataOperation>() {
            @Override
            protected void executeWithoutResults(SampledDataOperation operation) {
                dataAccess.delete(operation.getSample());
            }
        })).execute();
    }

    @Override
    public <E, K extends Serializable> void delete(Class<E> entityType, K key) {
        given(new IdentifiableDataOperation(dataAccess, OperationType.DELETE, entityType, key, new AbstractProceduralDataCallback<IdentifiableDataOperation>() {
            @Override
            protected void executeWithoutResults(IdentifiableDataOperation operation) {
                dataAccess.delete(operation.getEntityType(), operation.getKey());
            }
        })).execute();
    }

    @Override
    public <E> void deleteAll(Class<E> entityType) {
        given(new TypedDataOperation(dataAccess, OperationType.DELETE, entityType, new AbstractProceduralDataCallback<TypedDataOperation>() {
            @Override
            protected void executeWithoutResults(TypedDataOperation operation) {
                dataAccess.deleteAll(operation.getEntityType());
            }
        })).execute();
    }

    @Override
    public <E> void truncate(Class<E> entityType) {
        given(new TypedDataOperation(dataAccess, OperationType.TRUNCATE, entityType, new AbstractProceduralDataCallback<TypedDataOperation>() {
            @Override
            protected void executeWithoutResults(TypedDataOperation operation) {
                dataAccess.truncate(operation.getEntityType());
            }
        })).execute();
    }

    @Override
    public <E> List<E> find(E sample) {
        return (List<E>) given(new SampledDataOperation(dataAccess, OperationType.FIND, sample, new AbstractDefaultDataCallback<SampledDataOperation>() {
            @Override
            public Object execute(SampledDataOperation operation) {
                return dataAccess.find(operation.getSample());
            }
        })).execute();
    }

    @Override
    public <E> List<E> find(E sample, final String order) {
        return (List<E>) given(new SampledDataOperation(dataAccess, OperationType.FIND, sample, new AbstractDefaultDataCallback<SampledDataOperation>() {
            @Override
            public Object execute(SampledDataOperation operation) {
                return dataAccess.find(operation.getSample(), order);
            }
        })).execute();
    }

    @Override
    public <E> List<E> find(E sample, final int pageSize, final int pageNumber) {
        return (List<E>) given(new SampledDataOperation(dataAccess, OperationType.FIND, sample, new AbstractDefaultDataCallback<SampledDataOperation>() {
            @Override
            public Object execute(SampledDataOperation operation) {
                return dataAccess.find(operation.getSample(), pageSize, pageNumber);
            }
        })).execute();
    }

    @Override
    public <E> List<E> find(E sample, final String order, final int pageSize, final int pageNumber) {
        return (List<E>) given(new SampledDataOperation(dataAccess, OperationType.FIND, sample, new AbstractDefaultDataCallback<SampledDataOperation>() {
            @Override
            public Object execute(SampledDataOperation operation) {
                return dataAccess.find(operation.getSample(), order, pageSize, pageNumber);
            }
        })).execute();
    }

    @Override
    public <E, K extends Serializable> E find(Class<E> entityType, K key) {
        return (E) given(new IdentifiableDataOperation(dataAccess, OperationType.FIND, entityType, key, new AbstractDefaultDataCallback<IdentifiableDataOperation>() {
            @Override
            public Object execute(IdentifiableDataOperation operation) {
                return dataAccess.find(operation.getEntityType(), operation.getKey());
            }
        })).execute();
    }

    @Override
    public <E> List<E> findAll(Class<E> entityType) {
        return (List<E>) given(new TypedDataOperation(dataAccess, OperationType.FIND, entityType, new AbstractDefaultDataCallback<TypedDataOperation>() {
            @Override
            public Object execute(TypedDataOperation operation) {
                return dataAccess.findAll(operation.getEntityType());
            }
        })).execute();
    }

    @Override
    public <E> List<E> findAll(Class<E> entityType, final String order) {
        return (List<E>) given(new TypedDataOperation(dataAccess, OperationType.FIND, entityType, new AbstractDefaultDataCallback<TypedDataOperation>() {
            @Override
            public Object execute(TypedDataOperation operation) {
                return dataAccess.findAll(operation.getEntityType(), order);
            }
        })).execute();
    }

    @Override
    public <E> List<E> findAll(Class<E> entityType, final String order, final int pageSize, final int pageNumber) {
        return (List<E>) given(new TypedDataOperation(dataAccess, OperationType.FIND, entityType, new AbstractDefaultDataCallback<TypedDataOperation>() {
            @Override
            public Object execute(TypedDataOperation operation) {
                return dataAccess.findAll(operation.getEntityType(), order, pageSize, pageNumber);
            }
        })).execute();
    }

    @Override
    public <E> List<E> findAll(Class<E> entityType, final int pageSize, final int pageNumber) {
        return (List<E>) given(new TypedDataOperation(dataAccess, OperationType.FIND, entityType, new AbstractDefaultDataCallback<TypedDataOperation>() {
            @Override
            public Object execute(TypedDataOperation operation) {
                return dataAccess.findAll(operation.getEntityType(), pageSize, pageNumber);
            }
        })).execute();
    }

    @Override
    public <E> int executeUpdate(Class<E> entityType, String queryName, Map<String, Object> values) {
        return (Integer) given(new TypedQueryDataOperation(dataAccess, OperationType.NAMED_UPDATE, entityType, queryName, values, new AbstractDefaultDataCallback<TypedQueryDataOperation>() {
            @Override
            public Object execute(TypedQueryDataOperation operation) {
                return dataAccess.executeUpdate(operation.getEntityType(), operation.getQueryName(), operation.getMap());
            }
        })).execute();
    }

    @Override
    public <E> int executeUpdate(E sample, String queryName) {
        return (Integer) given(new SampledQueryDataOperation(dataAccess, OperationType.NAMED_UPDATE, sample, queryName, new AbstractDefaultDataCallback<SampledQueryDataOperation>() {
            @Override
            public Object execute(SampledQueryDataOperation operation) {
                return dataAccess.executeUpdate(operation.getSample(), operation.getQueryName());
            }
        })).execute();
    }

    @Override
    public <E> List<E> executeQuery(Class<E> entityType, String queryName, Map<String, Object> values) {
        return (List<E>) given(new TypedQueryDataOperation(dataAccess, OperationType.NAMED_QUERY, entityType, queryName, values, new AbstractDefaultDataCallback<TypedQueryDataOperation>() {
            @Override
            public Object execute(TypedQueryDataOperation operation) {
                return dataAccess.executeQuery(operation.getEntityType(), operation.getQueryName(), operation.getMap());
            }
        })).execute();
    }

    @Override
    public <E> List<E> executeQuery(E sample, String queryName) {
        return (List<E>) given(new SampledQueryDataOperation(dataAccess, OperationType.NAMED_QUERY, sample, queryName, new AbstractDefaultDataCallback<SampledQueryDataOperation>() {
            @Override
            public Object execute(SampledQueryDataOperation operation) {
                return dataAccess.executeQuery(operation.getSample(), operation.getQueryName());
            }
        })).execute();
    }

    @Override
    public <E> List<?> call(Class<E> entityType, String procedureName, Object... parameters) {
        return (List<?>) given(new ProcedureCallDataOperation(dataAccess, OperationType.PROCEDURE, entityType, procedureName, parameters, new AbstractDefaultDataCallback<ProcedureCallDataOperation>() {
            @Override
            public Object execute(ProcedureCallDataOperation operation) {
                return dataAccess.call(operation.getEntityType(), operation.getProcedureName(), operation.getParameters());
            }
        })).execute();
    }

    @Override
    public <E> long countAll(Class<E> entityType) {
        return (Long) given(new TypedDataOperation(dataAccess, OperationType.COUNT, entityType, new AbstractDefaultDataCallback<TypedDataOperation>() {
            @Override
            public Object execute(TypedDataOperation operation) {
                return dataAccess.countAll(operation.getEntityType());
            }
        })).execute();
    }

    @Override
    public <E> long count(E sample) {
        return (Long) given(new SampledDataOperation(dataAccess, OperationType.COUNT, sample, new AbstractDefaultDataCallback<SampledDataOperation>() {
            @Override
            public Object execute(SampledDataOperation operation) {
                return dataAccess.count(operation.getSample());
            }
        })).execute();
    }

    @Override
    public <E> boolean exists(E sample) {
        return (Boolean) given(new SampledDataOperation(dataAccess, OperationType.EXISTS, sample, new AbstractDefaultDataCallback<SampledDataOperation>() {
            @Override
            public Object execute(SampledDataOperation operation) {
                return dataAccess.exists(operation.getSample());
            }
        })).execute();
    }

    @Override
    public <E, K extends Serializable> boolean exists(Class<E> entityType, K key) {
        return (Boolean) given(new IdentifiableDataOperation(dataAccess, OperationType.EXISTS, entityType, key, new AbstractDefaultDataCallback<IdentifiableDataOperation>() {
            @Override
            public Object execute(IdentifiableDataOperation operation) {
                return dataAccess.exists(operation.getEntityType(), operation.getKey());
            }
        })).execute();
    }

    @Override
    public List<Integer> run(final BatchOperation batchOperation) {
        return dataAccess.run(new BatchOperation() {
            @Override
            public void execute(DataAccess dataAccess) {
                batchOperation.execute(DelegatingDataAccess.this);
            }
        });
    }

    @Override
    public <E> SelectQueryInitiator<E> from(E alias) {
        if (dataAccess instanceof FluentDataAccess) {
            return ((FluentDataAccess) dataAccess).from(alias);
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public <O> List<O> executeQuery(O sample) {
        if (dataAccess instanceof PartialDataAccess) {
            return ((PartialDataAccess) dataAccess).executeQuery(sample);
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public <O> List<O> executeQuery(Class<O> resultType) {
        if (dataAccess instanceof PartialDataAccess) {
            return ((PartialDataAccess) dataAccess).executeQuery(resultType);
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public <O> List<O> executeQuery(Class<O> resultType, Map<String, Object> values) {
        if (dataAccess instanceof PartialDataAccess) {
            return ((PartialDataAccess) dataAccess).executeQuery(resultType, values);
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public <E> List<Map<String, Object>> executeUntypedQuery(Class<E> entityType, String queryName, Map<String, Object> values) {
        if (dataAccess instanceof PartialDataAccess) {
            return ((PartialDataAccess) dataAccess).executeUntypedQuery(entityType, queryName, values);
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public <E, R> List<R> executeTypedQuery(Class<E> entityType, String queryName, Class<R> resultType, Map<String, Object> values) {
        if (dataAccess instanceof PartialDataAccess) {
            return ((PartialDataAccess) dataAccess).executeTypedQuery(entityType, queryName, resultType, values);
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public <E> List<Object> executeTypedQuery(Class<E> entityType, String queryName, Map<String, Object> values) {
        if (dataAccess instanceof PartialDataAccess) {
            return ((PartialDataAccess) dataAccess).executeTypedQuery(entityType, queryName, values);
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public <O> int executeUpdate(O sample) {
        if (dataAccess instanceof PartialDataAccess) {
            return ((PartialDataAccess) dataAccess).executeUpdate(sample);
        }
        throw new UnsupportedOperationException();
    }

}
