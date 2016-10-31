package com.mmnaseri.dragonfly.runtime.ext.audit.impl;

import com.mmnaseri.dragonfly.events.EventHandlerContext;
import com.mmnaseri.dragonfly.events.EventHandlerContextPostProcessor;
import com.mmnaseri.dragonfly.runtime.ext.audit.api.UserContext;

/**
 * @author Milad Naseri (mmnaseri@programmer.net)
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
