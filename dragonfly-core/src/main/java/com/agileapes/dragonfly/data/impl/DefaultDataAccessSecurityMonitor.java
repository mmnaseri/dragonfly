package com.agileapes.dragonfly.data.impl;

import com.agileapes.dragonfly.cg.EnhancementUtils;
import com.agileapes.dragonfly.cg.InterfaceInterceptor;
import com.agileapes.dragonfly.security.DataSecurityManager;
import com.agileapes.dragonfly.security.impl.ImmutableActor;
import com.agileapes.dragonfly.security.impl.MethodSubject;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/9, 16:09)
 */
public class DefaultDataAccessSecurityMonitor extends InterfaceInterceptor {

    private final DataSecurityManager securityManager;

    public DefaultDataAccessSecurityMonitor(DataSecurityManager securityManager) {
        this.securityManager = securityManager;
    }

    @Override
    protected Object call(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        final Method callerMethod = EnhancementUtils.getCallerMethod();
        if (!securityManager.isAllowed(new ImmutableActor(callerMethod.getDeclaringClass(), callerMethod), new MethodSubject(method))) {
            throw new SecurityException("Access denied to " + method + " for " + callerMethod);
        }
        return proxy.invokeSuper(obj, args);
    }
}
