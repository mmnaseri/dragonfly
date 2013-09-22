package com.agileapes.dragonfly.sample.audit;

import com.agileapes.dragonfly.data.DataAccess;
import com.agileapes.dragonfly.data.DataAccessPostProcessor;
import com.agileapes.dragonfly.events.EventHandlerContext;
import com.agileapes.dragonfly.events.impl.AbstractDataAccessEventHandler;
import com.agileapes.dragonfly.sample.user.UserContext;
import com.agileapes.dragonfly.sample.user.UserContextAware;

import java.util.Date;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/13, 12:59)
 */
public class AuditInterceptor implements DataAccessPostProcessor, UserContextAware {

    private UserContext userContext;

    @Override
    public void postProcessDataAccess(DataAccess dataAccess) {
        ((EventHandlerContext) dataAccess).addHandler(new AbstractDataAccessEventHandler() {

            @Override
            public <E> void beforeInsert(E entity) {
                ((Auditable) entity).setInsertUser(userContext.getCurrentUser());
                ((Auditable) entity).setInsertTime(new Date());
                ((Auditable) entity).setUpdateCount(0);
            }

            @Override
            public <E> void beforeUpdate(E entity) {
                ((Auditable) entity).setUpdateUser(userContext.getCurrentUser());
                ((Auditable) entity).setUpdateTime(new Date());
                ((Auditable) entity).setUpdateCount(((Auditable) entity).getUpdateCount() + 1);
            }
        });
    }

    @Override
    public void setUserContext(UserContext userContext) {
        this.userContext = userContext;
    }

}
