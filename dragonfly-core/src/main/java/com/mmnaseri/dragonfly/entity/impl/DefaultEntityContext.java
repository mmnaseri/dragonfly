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

package com.mmnaseri.dragonfly.entity.impl;

import com.mmnaseri.couteau.basics.api.Cache;
import com.mmnaseri.couteau.basics.api.Transformer;
import com.mmnaseri.couteau.basics.api.impl.ConcurrentCache;
import com.mmnaseri.couteau.enhancer.api.ClassEnhancer;
import com.mmnaseri.couteau.enhancer.impl.GeneratingClassEnhancer;
import com.mmnaseri.couteau.lang.compiler.impl.DefaultDynamicClassCompiler;
import com.mmnaseri.couteau.lang.compiler.impl.SimpleJavaSourceCompiler;
import com.mmnaseri.couteau.reflection.cp.ConfigurableClassLoader;
import com.mmnaseri.couteau.reflection.util.ClassUtils;
import com.mmnaseri.dragonfly.cg.StaticNamingPolicy;
import com.mmnaseri.dragonfly.data.DataAccess;
import com.mmnaseri.dragonfly.data.DataAccessSession;
import com.mmnaseri.dragonfly.entity.*;
import com.mmnaseri.dragonfly.error.EntityContextReInitializationError;
import com.mmnaseri.dragonfly.metadata.TableMetadata;
import com.mmnaseri.dragonfly.metadata.TableMetadataAware;
import com.mmnaseri.dragonfly.metadata.TableMetadataRegistry;
import com.mmnaseri.dragonfly.security.DataSecurityManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

import static com.mmnaseri.couteau.basics.collections.CollectionWrapper.with;

/**
 * This is a caching entity context that will use a generating class enhancer for enhancement
 * purposes.
 *
 * @author Milad Naseri (mmnaseri@programmer.net)
 * @since 1.0 (2013/9/5, 15:24)
 */
public class DefaultEntityContext implements ModifiableEntityContext {

    private static final Log log = LogFactory.getLog(DefaultEntityContext.class);
    private final String key;
    private DataAccess dataAccess;
    private EntityHandlerContext handlerContext;
    private final Map<Class<?>, Map<Class<?>, Class<?>>> interfaces;
    private final DataSecurityManager securityManager;
    private final TableMetadataRegistry tableMetadataRegistry;
    private final Cache<Class<?>, EntityFactory<?>> cache;
    private final DataAccessSession session;
    private ClassLoader defaultClassLoader;

    public DefaultEntityContext(DataSecurityManager securityManager, TableMetadataRegistry tableMetadataRegistry, DataAccessSession session) {
        this.securityManager = securityManager;
        this.tableMetadataRegistry = tableMetadataRegistry;
        this.session = session;
        this.key = UUID.randomUUID().toString();
        this.interfaces = new HashMap<Class<?>, Map<Class<?>, Class<?>>>();
        this.cache = new ConcurrentCache<Class<?>, EntityFactory<?>>();
        this.defaultClassLoader = null;
    }

    @Override
    public <E> E getInstance(Class<E> entityType) {
        return getInstance(tableMetadataRegistry.getTableMetadata(entityType));
    }

    @Override
    public <E> E getInstance(TableMetadata<E> tableMetadata) {
        final EntityHandler<E> entityHandler = handlerContext.getHandler(tableMetadata.getEntityType());
        final EntityProxy<E> entityProxy = new EntityProxy<E>(securityManager, tableMetadata, entityHandler, dataAccess, session, this);
        if (interfaces.containsKey(tableMetadata.getEntityType())) {
            final Map<Class<?>, Class<?>> classMap = interfaces.get(tableMetadata.getEntityType());
            for (Map.Entry<Class<?>, Class<?>> entry : classMap.entrySet()) {
                entityProxy.addInterface(entry.getKey(), entry.getValue());
            }
        }
        final E entity = enhanceObject(tableMetadata.getEntityType(), entityProxy);
        if (entity instanceof InitializedEntity<?>) {
            //noinspection unchecked
            InitializedEntity<E> initializedEntity = (InitializedEntity<E>) entity;
            initializedEntity.initialize(tableMetadata.getEntityType(), entity, key);
        }
        if (entity instanceof EntityAware) {
            ((EntityAware) entity).setEntity(entity);
        }
        if (entity instanceof EntityHandlerAware<?>) {
            //noinspection unchecked
            ((EntityHandlerAware<E>) entity).setEntityHandler(handlerContext.getHandler(entity));
        }
        if (entity instanceof TableMetadataAware<?>) {
            //noinspection unchecked
            ((TableMetadataAware<E>) entity).setTableMetadata(tableMetadata);
        }
        if (entity instanceof EntityContextAware) {
            ((EntityContextAware) entity).setEntityContext(this);
        }
        return entity;
    }

    private synchronized <E> E enhanceObject(Class<E> type, EntityProxy<E> entityProxy) {
        final EntityFactory<E> factory = getFactory(type, entityProxy);
        return factory.getInstance(entityProxy);
    }

    private <E> EntityFactory<E> getFactory(Class<E> entityType, EntityProxy<E> entityProxy) {
        if (cache.contains(entityType)) {
            //noinspection unchecked
            return (EntityFactory<E>) cache.read(entityType);
        }
        final Class<? extends E> enhancedType = enhanceClass(entityType, entityProxy);
//        noinspection unchecked
        final EntityFactoryBuilder<E> builder = new EntityFactoryBuilder<E>(entityType, enhancedType);
        final EntityFactory<E> entityFactory = builder.getEntityFactory();
        cache.write(entityType, entityFactory);
        return entityFactory;
    }

    private <E> Class<? extends E> enhanceClass(Class<E> original, EntityProxy<E> entityProxy) {
        final StaticNamingPolicy namingPolicy = new StaticNamingPolicy("Entity");
        final String className = namingPolicy.getClassName(original, null);
        Class<? extends E> enhancedClass = null;
        try {
            enhancedClass = ((Class<?>) ClassUtils.forName(className, original.getClassLoader())).asSubclass(original);
        } catch (ClassNotFoundException ignored) {
        }
        if (enhancedClass != null) {
            return enhancedClass;
        }
        final ClassEnhancer<E> classEnhancer = new GeneratingClassEnhancer<E>(defaultClassLoader);
        if (defaultClassLoader == null) {
            log.warn("No class loader has been specified for the entity enhancer. This is likely to cause issues when " +
                    "using data access in a container (such as a web application container)");
        } else {
            final GeneratingClassEnhancer<?> enhancer = (GeneratingClassEnhancer<?>) classEnhancer;
            final DefaultDynamicClassCompiler compiler = new DefaultDynamicClassCompiler(defaultClassLoader);
            final String classPath = getClassPath(defaultClassLoader);
            log.debug("Using classpath " + classPath);
            compiler.setOption(SimpleJavaSourceCompiler.Option.CLASSPATH, classPath);
            enhancer.setCompiler(compiler);
        }
        classEnhancer.setInterfaces(entityProxy.getInterfaces());
        classEnhancer.setSuperClass(original);
        classEnhancer.setNamingPolicy(namingPolicy);
        return classEnhancer.enhance();
    }

    /**
     * This method will determine the classpath argument passed to the JavaC compiler used by this
     * implementation
     *
     * @return the classpath
     */
    protected String getClassPath(ClassLoader classLoader) {
        final Set<File> classPathElements = new HashSet<File>();
        //Adding runtime dependencies available to the project
        final URL[] urls;
        if (classLoader instanceof URLClassLoader) {
            urls = ((URLClassLoader) classLoader).getURLs();
        } else if (classLoader instanceof ConfigurableClassLoader) {
            urls = ((ConfigurableClassLoader) classLoader).getUrls();
        } else {
            urls = new URL[0];
        }
        for (URL url : urls) {
            try {
                final File file = new File(url.toURI());
                if (file.exists()) {
                    classPathElements.add(file);
                }
            } catch (Throwable ignored) {
            }
        }
        return with(classPathElements).transform(new Transformer<File, String>() {
            @Override
            public String map(File input) {
                return input.getAbsolutePath();
            }
        }).join(File.pathSeparator);
    }

    @Override
    public <E> boolean has(E entity) {
        if (entity instanceof InitializedEntity) {
            InitializedEntity initializedEntity = (InitializedEntity) entity;
            return key.equals(initializedEntity.getToken());
        }
        return false;
    }

    @Override
    public void initialize(DataAccess dataAccess) {
        if (isInitialized()) {
            throw new EntityContextReInitializationError();
        }
        this.dataAccess = dataAccess;
    }

    @Override
    public boolean isInitialized() {
        return dataAccess != null;
    }

    @Override
    public void setInterfaces(Map<Class<?>, Map<Class<?>, Class<?>>> interfaces) {
        this.interfaces.clear();
        this.interfaces.putAll(interfaces);
    }

    @Override
    public void setEntityFactories(Map<Class<?>, EntityFactory<?>> factories) {
        for (Map.Entry<Class<?>, EntityFactory<?>> entry : factories.entrySet()) {
            cache.write(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void setDefaultClassLoader(ClassLoader classLoader) {
        defaultClassLoader = classLoader;
    }

    public void setHandlerContext(EntityHandlerContext handlerContext) {
        this.handlerContext = handlerContext;
    }

    public DataAccess getDataAccess() {
        return dataAccess;
    }

}
