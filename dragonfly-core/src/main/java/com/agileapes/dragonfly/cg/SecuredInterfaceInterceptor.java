/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2013 AgileApes, Ltd.
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

package com.agileapes.dragonfly.cg;

import com.mmnaseri.couteau.enhancer.api.MethodDescriptor;
import com.mmnaseri.couteau.enhancer.api.MethodProxy;
import com.mmnaseri.couteau.enhancer.impl.InterfaceInterceptor;
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
