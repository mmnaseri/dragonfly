package com.agileapes.dragonfly.entity;

/**
 * This interface builds on the {@link EntityContext}'s initialization abilities to allow for
 * dynamic specification of new interfaces to be assigned to the dispensed entities as they
 * are generated anew.
 *
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/8, 21:24)
 */
public interface ModifiableEntityContext extends EntityContext {

    /**
     * Adds a new interface to the context. All entities instantiated through the context
     * from this point on must be children of the given interface
     * @param ifc               the interface
     * @param implementation    the implementation, for delegation purposes
     * @param <I>               the type of the interface
     */
    <I> void addInterface(Class<I> ifc, Class<? extends I> implementation);

}
