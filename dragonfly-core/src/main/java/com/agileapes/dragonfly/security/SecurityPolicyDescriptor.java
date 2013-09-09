package com.agileapes.dragonfly.security;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/9, 16:52)
 */
public interface SecurityPolicyDescriptor {

    String getName();

    ActorFilter getActorFilter();

    SubjectFilter getSubjectFilter();

    SecurityPolicy getSecurityPolicy();

}
