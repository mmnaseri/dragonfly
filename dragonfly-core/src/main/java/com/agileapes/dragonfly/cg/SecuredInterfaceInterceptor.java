package com.agileapes.dragonfly.cg;

import com.agileapes.couteau.enhancer.api.MethodDescriptor;
import com.agileapes.couteau.enhancer.api.MethodProxy;
import com.agileapes.couteau.enhancer.impl.InterfaceInterceptor;
import com.agileapes.dragonfly.security.DataSecurityManager;
import com.agileapes.dragonfly.security.impl.MethodSubject;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/10, 23:35)
 */
public class SecuredInterfaceInterceptor extends InterfaceInterceptor {

    private final DataSecurityManager securityManager;

    public SecuredInterfaceInterceptor(DataSecurityManager securityManager) {
        this.securityManager = securityManager;
    }

    @Override
    public Object intercept(MethodDescriptor methodDescriptor, Object target, Object[] arguments, MethodProxy methodProxy) throws Throwable {
        securityManager.checkAccess(new MethodSubject(methodDescriptor));
        return super.intercept(methodDescriptor, target, arguments, methodProxy);
    }

    @Override
    protected Object call(MethodDescriptor methodDescriptor, Object target, Object[] arguments, MethodProxy methodProxy) throws Throwable {
        return methodProxy.callSuper(target, arguments);
    }
}
