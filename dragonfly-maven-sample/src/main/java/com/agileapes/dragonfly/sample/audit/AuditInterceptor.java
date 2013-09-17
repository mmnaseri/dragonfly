package com.agileapes.dragonfly.sample.audit;

import com.agileapes.dragonfly.data.DataAccess;
import com.agileapes.dragonfly.data.DataAccessPostProcessor;
import com.agileapes.dragonfly.entity.ModifiableEntityContext;
import com.agileapes.dragonfly.events.EventHandlerContext;
import com.agileapes.dragonfly.events.impl.AbstractDataAccessEventHandler;
import com.agileapes.dragonfly.metadata.ColumnMetadata;
import com.agileapes.dragonfly.metadata.TableMetadata;
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
public class AuditInterceptor implements DataAccessPostProcessor, UserContextAware {

    private UserContext userContext;

    @Override
    public void postProcessDataAccess(DataAccess dataAccess) {
        ((ModifiableEntityContext) dataAccess).addInterface(Auditable.class, DefaultAuditable.class);
        ((ModifiableEntityContext) dataAccess).addInterface(Identifiable.class, DefaultAuditable.class);
        ((EventHandlerContext) dataAccess).addHandler(new AbstractDataAccessEventHandler() {

            @Override
            public <E> void beforeInsert(E entity) {
                ((Auditable) entity).setInsertUser(userContext.getCurrentUser());
                ((Auditable) entity).setInsertTime(new Date());
            }

            @Override
            public <E> void beforeUpdate(E entity) {
                ((Auditable) entity).setUpdateUser(userContext.getCurrentUser());
                ((Auditable) entity).setUpdateTime(new Date());
                ((Auditable) entity).setUpdateCount(((Auditable) entity).getUpdateCount() + 1);
            }
        });
    }

    public <E> TableMetadata<E> intercept(TableMetadata<E> tableMetadata) {
        final Collection<ColumnMetadata> columns = new HashSet<ColumnMetadata>(tableMetadata.getColumns());
        columns.add(new ResolvedColumnMetadata(tableMetadata, Auditable.class, "insert_user", Types.VARCHAR, "insertUser", String.class, false, 256, 0, 0));
        columns.add(new ResolvedColumnMetadata(tableMetadata, Auditable.class, "insert_time", Types.TIMESTAMP, "insertTime", Timestamp.class, false, 0, 0, 0));
        columns.add(new ResolvedColumnMetadata(tableMetadata, Auditable.class, "update_user", Types.VARCHAR, "updateUser", String.class, true, 256, 0, 0));
        columns.add(new ResolvedColumnMetadata(tableMetadata, Auditable.class, "update_time", Types.TIMESTAMP, "updateTime", Timestamp.class, true, 0, 0, 0));
        columns.add(new ResolvedColumnMetadata(tableMetadata, Auditable.class, "update_count", Types.INTEGER, "updateCount", Integer.class, false, 0, 0, 0));
        return new ResolvedTableMetadata<E>(tableMetadata.getEntityType(), tableMetadata.getSchema(), tableMetadata.getName(), tableMetadata.getConstraints(), columns, tableMetadata.getNamedQueries(), tableMetadata.getSequences(), tableMetadata.getProcedures(), tableMetadata.getForeignReferences());
    }

    @Override
    public void setUserContext(UserContext userContext) {
        this.userContext = userContext;
    }

}
