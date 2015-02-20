package com.agileapes.dragonfly.runtime.ext.audit.impl;

import com.agileapes.dragonfly.events.EventHandlerContext;
import com.agileapes.dragonfly.events.EventHandlerContextPostProcessor;
import com.agileapes.dragonfly.runtime.ext.audit.api.UserContext;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2/19/15, 8:58 PM)
 */
public class AuditInterceptor implements EventHandlerContextPostProcessor {

    private final UserContext userContext;

    public AuditInterceptor(UserContext userContext) {
        this.userContext = userContext;
    }

    @Override
    public void postProcessEventHandlerContext(EventHandlerContext eventHandlerContext) {
        eventHandlerContext.addHandler(new AuditEventHandler(userContext));
    }
}
