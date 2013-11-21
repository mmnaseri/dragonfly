/*
 * Copyright (c) 2013 AgileApes, Ltd.
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall
 * be included in all copies or substantial portions of the
 * Software.
 */

package com.agileapes.dragonfly.security.impl;

import com.agileapes.couteau.basics.api.Filter;
import com.agileapes.couteau.reflection.util.ClassUtils;
import com.agileapes.dragonfly.cg.EnhancementUtils;
import com.agileapes.dragonfly.security.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.agileapes.couteau.basics.collections.CollectionWrapper.with;

/**
 * <p>This is the default implementation of the {@link com.agileapes.dragonfly.security.DataSecurityManager}
 * interface.</p>
 *
 * <p>This implementation will not do anything when there is no policy applicable to the action taking
 * place.</p>
 *
 * <p>If there are policies applying to the subject, it then checks if there are any of them that has
 * anything to say about the given actor. Actors are determined by the method that is calling the data
 * operation.</p>
 *
 * <p>Should any such policies exist, it will then loop through them and in case of failures, will call
 * the {@link AccessDeniedHandler#handle(String, Actor, Subject)} method. At least one of the policies must
 * explicitly define an {@link PolicyDecisionType#ALLOW ALLOW} action to be taken for the action to not
 * fail.</p>
 *
 * <p>If none of the declaring policies allows the action to take place (i.e. they are all undecided) the
 * check will fail on general principle, with the name of the failing policy set to the canonical name of
 * this class.</p>
 *
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/9, 16:55)
 */
public class DefaultDataSecurityManager implements DataSecurityManager {

    private final AccessDeniedHandler accessDeniedHandler;
    private final Set<DataSecurityPolicy> policies = new HashSet<DataSecurityPolicy>();

    public DefaultDataSecurityManager(AccessDeniedHandler accessDeniedHandler) {
        this.accessDeniedHandler = accessDeniedHandler;
    }

    @Override
    public synchronized void addPolicy(DataSecurityPolicy policy) {
        policies.add(policy);
    }

    @Override
    public void checkAccess(final Subject subject) {
        synchronized (policies) {
            //noinspection unchecked
            final List<DataSecurityPolicy> applyingPolicies = with(policies)
                    .keep(new Filter<DataSecurityPolicy>() {
                        @Override
                        public boolean accepts(DataSecurityPolicy item) {
                            final Class<?> subjectType = ClassUtils.resolveTypeArgument(item.getSubjectFilter().getClass(), Filter.class);
                            return subjectType != null && subjectType.isInstance(subject);
                        }
                    })
                    .keep(new Filter<DataSecurityPolicy>() {
                        @Override
                        public boolean accepts(DataSecurityPolicy item) {
                            //noinspection unchecked
                            return item.getSubjectFilter().accepts(subject);
                        }
                    }).list();
            if (applyingPolicies.isEmpty()) {
                return;
            }
            final ImmutableActor actor = new ImmutableActor(EnhancementUtils.getCallerMethod());
            //noinspection unchecked
            final List<DataSecurityPolicy> declaringPolicies = with(applyingPolicies).keep(new Filter<DataSecurityPolicy>() {
                @Override
                public boolean accepts(DataSecurityPolicy item) {
                    return item.getActorFilter().accepts(actor);
                }
            }).list();
            boolean allowed = declaringPolicies.isEmpty();
            for (DataSecurityPolicy declaringPolicy : declaringPolicies) {
                final PolicyDecisionType policyDecisionType = declaringPolicy.getDecisionType();
                if (PolicyDecisionType.ALLOW.equals(policyDecisionType)) {
                    allowed = true;
                } else if (PolicyDecisionType.DENY.equals(policyDecisionType)) {
                    accessDeniedHandler.handle(declaringPolicy.getName(), actor, subject);
                }
            }
            if (!allowed) {
                accessDeniedHandler.handle(getClass().getCanonicalName(), actor, subject);
            }
        }
    }

}
