package com.agileapes.dragonfly.data.impl;

import com.agileapes.couteau.reflection.beans.BeanInitializer;
import com.agileapes.couteau.reflection.beans.impl.ConstructorBeanInitializer;
import com.agileapes.dragonfly.data.DataAccess;
import com.agileapes.dragonfly.data.DataAccessSession;
import com.agileapes.dragonfly.data.PartialDataAccess;
import com.agileapes.dragonfly.entity.EntityContext;
import com.agileapes.dragonfly.entity.EntityHandlerContext;
import com.agileapes.dragonfly.events.DataAccessEventHandler;
import com.agileapes.dragonfly.events.EventHandlerContext;
import com.agileapes.dragonfly.events.impl.CompositeDataAccessEventHandler;
import com.agileapes.dragonfly.metadata.impl.ColumnMappingMetadataCollector;
import com.agileapes.dragonfly.security.DataSecurityManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/20, 23:29)
 */
public class CachingDataAccess implements PartialDataAccess, EventHandlerContext {

    private static final Log log = LogFactory.getLog(DataAccess.class);
    private final DataAccessSession session;
    private final DataSecurityManager securityManager;
    private final EntityContext entityContext;
    private final EntityHandlerContext entityHandlerContext;
    private final BeanInitializer beanInitializer;
    private final ColumnMappingMetadataCollector metadataCollector;
    private final CompositeDataAccessEventHandler eventHandler;

    public CachingDataAccess(DataAccessSession session, DataSecurityManager securityManager, EntityContext entityContext, EntityHandlerContext entityHandlerContext) {
        this(session, securityManager, entityContext, entityHandlerContext, true);
    }

    public CachingDataAccess(DataAccessSession session, DataSecurityManager securityManager, EntityContext entityContext, EntityHandlerContext entityHandlerContext, boolean autoInitialize) {
        this.session = session;
        this.securityManager = securityManager;
        this.entityContext = entityContext;
        this.entityHandlerContext = entityHandlerContext;
        this.beanInitializer = new ConstructorBeanInitializer();
        this.metadataCollector = new ColumnMappingMetadataCollector();
        this.eventHandler = new CompositeDataAccessEventHandler();
        if (autoInitialize) {
            log.info("Automatically initializing the session");
            synchronized (this.session) {
                if (!this.session.isInitialized()) {
                    this.session.initialize();
                }
            }
        }
    }

    @Override
    public <O> List<O> executeQuery(O sample) {
        return null;
    }

    @Override
    public <O> List<O> executeQuery(Class<O> resultType) {
        return null;
    }

    @Override
    public <O> List<O> executeQuery(Class<O> resultType, Map<String, Object> values) {
        return null;
    }

    @Override
    public <E> List<Map<String, Object>> executeUntypedQuery(Class<E> entityType, String queryName, Map<String, Object> values) {
        return null;
    }

    @Override
    public <O> int executeUpdate(O sample) {
        return 0;
    }

    @Override
    public <E> void save(E entity) {
    }

    @Override
    public <E> void delete(E entity) {
    }

    @Override
    public <E, K extends Serializable> void delete(Class<E> entityType, K key) {
    }

    @Override
    public <E> void deleteAll(Class<E> entityType) {
    }

    @Override
    public <E> void truncate(Class<E> entityType) {
    }

    @Override
    public <E> List<E> find(E sample) {
        return null;
    }

    @Override
    public <E, K extends Serializable> E find(Class<E> entityType, K key) {
        return null;
    }

    @Override
    public <E> List<E> findAll(Class<E> entityType) {
        return null;
    }

    @Override
    public <E> int executeUpdate(Class<E> entityType, String queryName, Map<String, Object> values) {
        return 0;
    }

    @Override
    public <E> int executeUpdate(E sample, String queryName) {
        return 0;
    }

    @Override
    public <E> List<E> executeQuery(Class<E> entityType, String queryName, Map<String, Object> values) {
        return null;
    }

    @Override
    public <E> List<E> executeQuery(E sample, String queryName) {
        return null;
    }

    @Override
    public <E> List<?> call(Class<E> entityType, String procedureName, Object... parameters) {
        return null;
    }

    @Override
    public void addHandler(DataAccessEventHandler eventHandler) {
    }

}
