package com.agileapes.dragonfly.entity;

import java.util.Map;

/**
 * This interface builds on the {@link EntityContext}'s initialization abilities to allow for
 * dynamic specification of new interfaces to be assigned to the dispensed entities as they
 * are generated anew.
 *
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/8, 21:24)
 */
public interface ModifiableEntityContext extends EntityContext {

    void setInterfaces(Map<Class<?>, Map<Class<?>, Class<?>>> interfaces);

    void setEntityFactories(Map<Class<?>, EntityFactory<?>> factories);

}
