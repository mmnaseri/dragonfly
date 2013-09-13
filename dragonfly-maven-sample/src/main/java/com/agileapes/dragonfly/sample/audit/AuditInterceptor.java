package com.agileapes.dragonfly.sample.audit;

import com.agileapes.dragonfly.data.DataAccess;
import com.agileapes.dragonfly.entity.ModifiableEntityContext;
import com.agileapes.dragonfly.events.DataAccessPostProcessor;
import com.agileapes.dragonfly.events.EventHandlerContext;
import com.agileapes.dragonfly.events.impl.AbstractDataAccessEventHandler;
import com.agileapes.dragonfly.metadata.ColumnMetadata;
import com.agileapes.dragonfly.metadata.TableMetadata;
import com.agileapes.dragonfly.metadata.TableMetadataInterceptor;
import com.agileapes.dragonfly.metadata.impl.ResolvedColumnMetadata;
import com.agileapes.dragonfly.metadata.impl.ResolvedTableMetadata;
import com.agileapes.dragonfly.sample.user.UserContext;
import com.agileapes.dragonfly.sample.user.UserContextAware;

import java.sql.Timestamp;
import java.sql.Types;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/13, 12:59)
 */
public class AuditInterceptor implements DataAccessPostProcessor, TableMetadataInterceptor, UserContextAware {

    private UserContext userContext;

    @Override
    public void postProcessAfterInitialization(DataAccess dataAccess) {
        ((ModifiableEntityContext) dataAccess).addInterface(Auditable.class, DefaultAuditable.class);
        ((EventHandlerContext) dataAccess).addHandler(new AbstractDataAccessEventHandler() {
            @Override
            public <E> void beforeUpdate(E entity) {
                ((Auditable) entity).setUpdateUser(userContext.getCurrentUser());
                ((Auditable) entity).setUpdateTime(new Timestamp(new Date().getTime()));
            }
        });
    }

    @Override
    public <E> TableMetadata<E> intercept(TableMetadata<E> tableMetadata) {
        final Collection<ColumnMetadata> columns = new HashSet<ColumnMetadata>(tableMetadata.getColumns());
        columns.add(new ResolvedColumnMetadata(tableMetadata, "update_user", Types.VARCHAR, "updateUser", String.class, true, 256, 0, 0));
        columns.add(new ResolvedColumnMetadata(tableMetadata, "update_time", Types.TIMESTAMP, "updateTime", Timestamp.class, true, 256, 0, 0));
        return new ResolvedTableMetadata<E>(tableMetadata.getEntityType(), tableMetadata.getSchema(), tableMetadata.getName(), tableMetadata.getConstraints(), columns, tableMetadata.getNamedQueries(), tableMetadata.getSequences(), tableMetadata.getProcedures(), tableMetadata.getForeignReferences());
    }

    @Override
    public void setUserContext(UserContext userContext) {
        this.userContext = userContext;
    }

}
