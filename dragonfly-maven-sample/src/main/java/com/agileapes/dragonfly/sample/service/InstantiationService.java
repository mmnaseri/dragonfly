package com.agileapes.dragonfly.sample.service;

import com.agileapes.dragonfly.data.DataAccessSession;
import com.agileapes.dragonfly.data.impl.SecuredDataAccess;
import com.agileapes.dragonfly.dialect.impl.Mysql5Dialect;
import com.agileapes.dragonfly.entity.EntityContext;
import com.agileapes.dragonfly.entity.impl.DefaultEntityHandlerContext;
import com.agileapes.dragonfly.entity.impl.EntityProxy;
import com.agileapes.dragonfly.entity.impl.GenericEntityHandler;
import com.agileapes.dragonfly.metadata.MetadataRegistry;
import com.agileapes.dragonfly.metadata.impl.DefaultMetadataRegistry;
import com.agileapes.dragonfly.sample.entities.Person;
import com.agileapes.dragonfly.security.impl.DefaultDataSecurityManager;
import com.agileapes.dragonfly.security.impl.FatalAccessDeniedHandler;
import com.agileapes.dragonfly.statement.impl.StatementRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/15, 16:56)
 */
@Service
public class InstantiationService {

    @Autowired
    private EntityContext entityContext;

    @Autowired
    private MetadataRegistry metadataRegistry;

    public void execute() {
        long normalTime = System.nanoTime();
        final int benchmarkSize = 10000;
        final DefaultDataSecurityManager securityManager = new DefaultDataSecurityManager(new FatalAccessDeniedHandler());
        final SecuredDataAccess dataAccess = new SecuredDataAccess(new DataAccessSession(new Mysql5Dialect(), new StatementRegistry(), new DefaultMetadataRegistry()), securityManager, entityContext, new DefaultEntityHandlerContext(null, null));
        for (int i = 0; i < benchmarkSize; i ++) {
            new EntityProxy<Person>(dataAccess, null, securityManager, new GenericEntityHandler<Person>(Person.class, entityContext, metadataRegistry.getTableMetadata(Person.class)), entityContext);
            new Person();
        }
        normalTime = System.nanoTime() - normalTime;
        long contextTime = System.nanoTime();
        for (int i = 0; i < benchmarkSize; i ++) {
            entityContext.getInstance(Person.class);
        }
        contextTime = System.nanoTime() - contextTime;
        System.out.println("Benchmark size: " + benchmarkSize);
        System.out.println("Normal time: " + normalTime);
        System.out.println("Context time: " + contextTime);
        System.out.println("[c/n] : " + (((double) contextTime) / normalTime));
    }

}
