package com.agileapes.dragonfly.security;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/9, 16:49)
 */
public interface DataSecurityManager {

    void addPolicy(String name, ActorFilter actorFilter, SubjectFilter subjectFilter, SecurityPolicy securityPolicy);

    void checkAccess(Subject subject);

}
