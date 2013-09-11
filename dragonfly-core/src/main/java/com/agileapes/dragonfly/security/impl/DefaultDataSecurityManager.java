package com.agileapes.dragonfly.security.impl;

import com.agileapes.couteau.basics.api.Filter;
import com.agileapes.couteau.reflection.util.ClassUtils;
import com.agileapes.dragonfly.cg.EnhancementUtils;
import com.agileapes.dragonfly.security.*;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.agileapes.couteau.basics.collections.CollectionWrapper.with;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/9, 16:55)
 */
public class DefaultDataSecurityManager implements DataSecurityManager {

    private final AccessDeniedHandler accessDeniedHandler;
    private final Set<SecurityPolicyDescriptor> policies = new HashSet<SecurityPolicyDescriptor>();

    public DefaultDataSecurityManager(AccessDeniedHandler accessDeniedHandler) {
        this.accessDeniedHandler = accessDeniedHandler;
    }

    @Override
    public synchronized void addPolicy(String name, ActorFilter actorFilter, SubjectFilter subjectFilter, SecurityPolicy securityPolicy) {
        policies.add(new ImmutableSecurityPolicyDescriptor(name, actorFilter, subjectFilter, securityPolicy));
    }

    @Override
    public void checkAccess(final Subject subject) {
        synchronized (policies) {
            //noinspection unchecked
            final List<SecurityPolicyDescriptor> applyingPolicies = with(policies)
                    .keep(new Filter<SecurityPolicyDescriptor>() {
                        @Override
                        public boolean accepts(SecurityPolicyDescriptor item) {
                            final Class<?> subjectType = ClassUtils.resolveTypeArgument(item.getSubjectFilter().getClass(), Filter.class);
                            return subjectType != null && subjectType.isInstance(subject);
                        }
                    })
                    .keep(new Filter<SecurityPolicyDescriptor>() {
                        @Override
                        public boolean accepts(SecurityPolicyDescriptor item) {
                            //noinspection unchecked
                            return item.getSubjectFilter().accepts(subject);
                        }
                    }).list();
            if (!applyingPolicies.isEmpty()) {
                final Method callerMethod = EnhancementUtils.getCallerMethod();
                final ImmutableActor actor = new ImmutableActor(callerMethod.getDeclaringClass(), callerMethod);
                //noinspection unchecked
                final List<SecurityPolicyDescriptor> declaringPolicies = with(applyingPolicies).keep(new Filter<SecurityPolicyDescriptor>() {
                    @Override
                    public boolean accepts(SecurityPolicyDescriptor item) {
                        return item.getActorFilter().accepts(actor);
                    }
                }).list();
                boolean allowed = declaringPolicies.isEmpty();
                for (SecurityPolicyDescriptor declaringPolicy : declaringPolicies) {
                    final SecurityPolicy securityPolicy = declaringPolicy.getSecurityPolicy();
                    if (SecurityPolicy.ALLOW.equals(securityPolicy)) {
                        allowed = true;
                    } else if (SecurityPolicy.DENY.equals(securityPolicy)) {
                        accessDeniedHandler.handle(declaringPolicy.getName(), actor, subject);
                    }
                }
                if (!allowed) {
                    accessDeniedHandler.handle(null, actor, subject);
                }
            }
        }
    }

}
