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

import com.agileapes.dragonfly.security.ActorFilter;
import com.agileapes.dragonfly.security.SecurityPolicy;
import com.agileapes.dragonfly.security.SecurityPolicyDescriptor;
import com.agileapes.dragonfly.security.SubjectFilter;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/9, 16:56)
 */
public class ImmutableSecurityPolicyDescriptor implements SecurityPolicyDescriptor {

    private final String name;
    private final ActorFilter actorFilter;
    private final SubjectFilter subjectFilter;
    private final SecurityPolicy securityPolicy;

    public ImmutableSecurityPolicyDescriptor(String name, ActorFilter actorFilter, SubjectFilter subjectFilter, SecurityPolicy securityPolicy) {
        this.name = name;
        this.actorFilter = actorFilter;
        this.subjectFilter = subjectFilter;
        this.securityPolicy = securityPolicy;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ActorFilter getActorFilter() {
        return actorFilter;
    }

    @Override
    public SubjectFilter getSubjectFilter() {
        return subjectFilter;
    }

    @Override
    public SecurityPolicy getSecurityPolicy() {
        return securityPolicy;
    }
}
