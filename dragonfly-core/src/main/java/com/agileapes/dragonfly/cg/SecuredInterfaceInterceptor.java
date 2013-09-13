package com.agileapes.dragonfly.cg;

import com.agileapes.couteau.enhancer.api.MethodDescriptor;
import com.agileapes.couteau.enhancer.api.MethodProxy;
import com.agileapes.couteau.enhancer.impl.InterfaceInterceptor;
import com.agileapes.dragonfly.security.DataSecurityManager;
import com.agileapes.dragonfly.security.impl.MethodSubject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This is an interceptor that will consult the security manager before any method
 * is called on the intercepted class.
 *
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/10, 23:35)
 */
public class SecuredInterfaceInterceptor extends InterfaceInterceptor {

    private final DataSecurityManager securityManager;
    private static final Log log = LogFactory.getLog(SecuredInterfaceInterceptor.class);

    public SecuredInterfaceInterceptor(DataSecurityManager securityManager) {
        this.securityManager = securityManager;
    }

    @Override
    public Object intercept(MethodDescriptor methodDescriptor, Object target, Object[] arguments, MethodProxy methodProxy) throws Throwable {
        log.info("Intercepting access to " + methodDescriptor);
        securityManager.checkAccess(new MethodSubject(methodDescriptor));
        log.info("Access granted");
        return super.intercept(methodDescriptor, target, arguments, methodProxy);
    }

    @Override
    protected Object call(MethodDescriptor methodDescriptor, Object target, Object[] arguments, MethodProxy methodProxy) throws Throwable {
        return methodProxy.callSuper(target, arguments);
    }
}
