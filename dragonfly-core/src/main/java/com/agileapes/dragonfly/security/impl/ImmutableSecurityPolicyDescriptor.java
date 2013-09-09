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
