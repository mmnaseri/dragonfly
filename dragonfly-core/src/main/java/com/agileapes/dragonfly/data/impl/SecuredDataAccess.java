package com.agileapes.dragonfly.data.impl;

import com.agileapes.couteau.enhancer.api.MethodDescriptor;
import com.agileapes.couteau.enhancer.impl.ImmutableMethodDescriptor;
import com.agileapes.dragonfly.data.DataAccess;
import com.agileapes.dragonfly.data.DataAccessSession;
import com.agileapes.dragonfly.data.PartialDataAccess;
import com.agileapes.dragonfly.entity.EntityHandlerContext;
import com.agileapes.dragonfly.entity.ModifiableEntityContext;
import com.agileapes.dragonfly.events.DataAccessEventHandler;
import com.agileapes.dragonfly.events.EventHandlerContext;
import com.agileapes.dragonfly.metadata.TableMetadata;
import com.agileapes.dragonfly.security.DataSecurityManager;
import com.agileapes.dragonfly.security.impl.MethodSubject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
public class SecuredDataAccess extends DefaultDataAccess implements PartialDataAccess, ModifiableEntityContext, EventHandlerContext {

    private static final Map<Integer, MethodDescriptor> methodDescriptors = new HashMap<Integer, MethodDescriptor>();
    static {
        methodDescriptors.put(0, new ImmutableMethodDescriptor(DefaultDataAccess.class, Serializable.class, "getKey", new Class[]{Object.class}, new Annotation[0]));
        methodDescriptors.put(1, new ImmutableMethodDescriptor(DefaultDataAccess.class, Object.class, "getInstance", new Class[]{Class.class}, new Annotation[0]));
        methodDescriptors.put(2, new ImmutableMethodDescriptor(DefaultDataAccess.class, Object.class, "getInstance", new Class[]{TableMetadata.class}, new Annotation[0]));
        methodDescriptors.put(3, new ImmutableMethodDescriptor(DefaultDataAccess.class, Object.class, "find", new Class[]{Class.class, Serializable.class}, new Annotation[0]));
        methodDescriptors.put(4, new ImmutableMethodDescriptor(DefaultDataAccess.class, List.class, "find", new Class[]{Object.class}, new Annotation[0]));
        methodDescriptors.put(5, new ImmutableMethodDescriptor(DefaultDataAccess.class, void.class, "save", new Class[]{Object.class}, new Annotation[0]));
        methodDescriptors.put(6, new ImmutableMethodDescriptor(DefaultDataAccess.class, void.class, "delete", new Class[]{Class.class, Serializable.class}, new Annotation[0]));
        methodDescriptors.put(7, new ImmutableMethodDescriptor(DefaultDataAccess.class, void.class, "delete", new Class[]{Object.class}, new Annotation[0]));
        methodDescriptors.put(8, new ImmutableMethodDescriptor(DefaultDataAccess.class, void.class, "truncate", new Class[]{Class.class}, new Annotation[0]));
        methodDescriptors.put(9, new ImmutableMethodDescriptor(DefaultDataAccess.class, boolean.class, "has", new Class[]{Object.class}, new Annotation[0]));
        methodDescriptors.put(10, new ImmutableMethodDescriptor(DefaultDataAccess.class, void.class, "deleteAll", new Class[]{Class.class}, new Annotation[0]));
        methodDescriptors.put(11, new ImmutableMethodDescriptor(DefaultDataAccess.class, List.class, "findAll", new Class[]{Class.class}, new Annotation[0]));
        methodDescriptors.put(12, new ImmutableMethodDescriptor(DefaultDataAccess.class, List.class, "call", new Class[]{Class.class, String.class, Object[].class}, new Annotation[0]));
        methodDescriptors.put(13, new ImmutableMethodDescriptor(DefaultDataAccess.class, int.class, "executeUpdate", new Class[]{Object.class, String.class}, new Annotation[0]));
        methodDescriptors.put(14, new ImmutableMethodDescriptor(DefaultDataAccess.class, int.class, "executeUpdate", new Class[]{Class.class, String.class, Map.class}, new Annotation[0]));
        methodDescriptors.put(15, new ImmutableMethodDescriptor(DefaultDataAccess.class, int.class, "executeUpdate", new Class[]{Object.class}, new Annotation[0]));
        methodDescriptors.put(16, new ImmutableMethodDescriptor(DefaultDataAccess.class, List.class, "executeQuery", new Class[]{Class.class, String.class, Map.class}, new Annotation[0]));
        methodDescriptors.put(17, new ImmutableMethodDescriptor(DefaultDataAccess.class, List.class, "executeQuery", new Class[]{Object.class}, new Annotation[0]));
        methodDescriptors.put(18, new ImmutableMethodDescriptor(DefaultDataAccess.class, List.class, "executeQuery", new Class[]{Class.class}, new Annotation[0]));
        methodDescriptors.put(19, new ImmutableMethodDescriptor(DefaultDataAccess.class, List.class, "executeQuery", new Class[]{Class.class, Map.class}, new Annotation[0]));
        methodDescriptors.put(20, new ImmutableMethodDescriptor(DefaultDataAccess.class, List.class, "executeQuery", new Class[]{Object.class, String.class}, new Annotation[0]));
        methodDescriptors.put(21, new ImmutableMethodDescriptor(DefaultDataAccess.class, void.class, "addInterface", new Class[]{Class.class, Class.class}, new Annotation[0]));
        methodDescriptors.put(22, new ImmutableMethodDescriptor(DefaultDataAccess.class, void.class, "addHandler", new Class[]{DataAccessEventHandler.class}, new Annotation[0]));
        methodDescriptors.put(23, new ImmutableMethodDescriptor(DefaultDataAccess.class, List.class, "executeUntypedQuery", new Class[]{Class.class, String.class, Map.class}, new Annotation[0]));
        methodDescriptors.put(24, new ImmutableMethodDescriptor(DefaultDataAccess.class, EntityHandlerContext.class, "getHandlerContext", new Class[]{}, new Annotation[0]));
    }

    private final DataSecurityManager securityManager;
    private static final Log log = LogFactory.getLog(DataAccess.class);

    public SecuredDataAccess(DataAccessSession session, DataSecurityManager securityManager) {
        this(session, securityManager, true);
    }

    public SecuredDataAccess(DataAccessSession session, DataSecurityManager securityManager, boolean autoInitialize) {
        super(session, securityManager, autoInitialize);
        this.securityManager = securityManager;
        log.info("Initializing secured data access interface");
    }

    @Override
    public Serializable getKey(final Object entity) {
        securityManager.checkAccess(new MethodSubject(methodDescriptors.get(0)));
        return super.getKey(entity);
    }

    @Override
    public Object getInstance(final Class entityType) {
        securityManager.checkAccess(new MethodSubject(methodDescriptors.get(1)));
        return super.getInstance(entityType);
    }

    @Override
    public Object getInstance(final TableMetadata tableMetadata) {
        securityManager.checkAccess(new MethodSubject(methodDescriptors.get(2)));
        return super.getInstance(tableMetadata);
    }

    @Override
    public Object find(final Class entityType, final Serializable key) {
        securityManager.checkAccess(new MethodSubject(methodDescriptors.get(3)));
        return super.find(entityType, key);
    }

    @Override
    public List find(final Object sample) {
        securityManager.checkAccess(new MethodSubject(methodDescriptors.get(4)));
        return super.find(sample);
    }

    @Override
    public void save(final Object entity) {
        securityManager.checkAccess(new MethodSubject(methodDescriptors.get(5)));
        super.save(entity);
    }

    @Override
    public void delete(final Class entityType, final Serializable key) {
        securityManager.checkAccess(new MethodSubject(methodDescriptors.get(6)));
        super.delete(entityType, key);
    }

    @Override
    public void delete(final Object sample) {
        securityManager.checkAccess(new MethodSubject(methodDescriptors.get(7)));
        super.delete(sample);
    }

    @Override
    public void truncate(final Class entityType) {
        securityManager.checkAccess(new MethodSubject(methodDescriptors.get(8)));
        super.truncate(entityType);
    }

    @Override
    public boolean has(final Object entity) {
        securityManager.checkAccess(new MethodSubject(methodDescriptors.get(9)));
        return super.has(entity);
    }

    @Override
    public void deleteAll(final Class entityType) {
        securityManager.checkAccess(new MethodSubject(methodDescriptors.get(10)));
        super.deleteAll(entityType);
    }

    @Override
    public List findAll(final Class entityType) {
        securityManager.checkAccess(new MethodSubject(methodDescriptors.get(11)));
        return super.findAll(entityType);
    }

    @Override
    public List call(final Class entityType, final String procedureName, final Object[] parameters) {
        securityManager.checkAccess(new MethodSubject(methodDescriptors.get(12)));
        return super.call(entityType, procedureName, parameters);
    }

    @Override
    public int executeUpdate(final Object sample, final String queryName) {
        securityManager.checkAccess(new MethodSubject(methodDescriptors.get(13)));
        return super.executeUpdate(sample, queryName);
    }

    @Override
    public int executeUpdate(final Class entityType, final String queryName, final Map values) {
        securityManager.checkAccess(new MethodSubject(methodDescriptors.get(14)));
        return super.executeUpdate(entityType, queryName, values);
    }

    @Override
    public int executeUpdate(final Object sample) {
        securityManager.checkAccess(new MethodSubject(methodDescriptors.get(15)));
        return super.executeUpdate(sample);
    }

    @Override
    public List executeQuery(final Class entityType, final String queryName, final Map values) {
        securityManager.checkAccess(new MethodSubject(methodDescriptors.get(16)));
        return super.executeQuery(entityType, queryName, values);
    }

    @Override
    public List executeQuery(final Object sample) {
        securityManager.checkAccess(new MethodSubject(methodDescriptors.get(17)));
        return super.executeQuery(sample);
    }

    @Override
    public List executeQuery(final Class resultType) {
        securityManager.checkAccess(new MethodSubject(methodDescriptors.get(18)));
        return super.executeQuery(resultType);
    }

    @Override
    public List executeQuery(final Class resultType, final Map values) {
        securityManager.checkAccess(new MethodSubject(methodDescriptors.get(19)));
        return super.executeQuery(resultType, values);
    }

    @Override
    public List executeQuery(final Object sample, final String queryName) {
        securityManager.checkAccess(new MethodSubject(methodDescriptors.get(20)));
        return super.executeQuery(sample, queryName);
    }

    @Override
    public void addInterface(final Class superType, final Class implementation) {
        securityManager.checkAccess(new MethodSubject(methodDescriptors.get(21)));
        super.addInterface(superType, implementation);
    }

    @Override
    public void addHandler(final DataAccessEventHandler eventHandler) {
        securityManager.checkAccess(new MethodSubject(methodDescriptors.get(22)));
        super.addHandler(eventHandler);
    }

    @Override
    public List executeUntypedQuery(final Class entityType, final String queryName, final Map values) {
        securityManager.checkAccess(new MethodSubject(methodDescriptors.get(23)));
        return super.executeUntypedQuery(entityType, queryName, values);
    }

    @Override
    public EntityHandlerContext getHandlerContext() {
        securityManager.checkAccess(new MethodSubject(methodDescriptors.get(24)));
        return super.getHandlerContext();
    }

    @Override
    public boolean equals(final Object that) {
        return super.equals(that);
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

}